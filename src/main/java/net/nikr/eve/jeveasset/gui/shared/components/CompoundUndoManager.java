/*
 * Copyright 2009-2022 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * Original code from https://tips4java.wordpress.com/2008/10/27/compound-undo-manager/
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.gui.shared.components;

import com.sun.jna.Platform;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/*
** This class will merge individual edits into a single larger edit.
** That is, characters entered sequentially will be grouped together and
** undone as a group. Any attribute changes will be considered as part
** of the group and will therefore be undone when the group is undone.
 */
public class CompoundUndoManager extends UndoManager
		implements UndoableEditListener, DocumentListener {

	private final UndoManager undoManager;
	private final JTextComponent textComponent;
	private final UndoAction undoAction;
	private final RedoAction redoAction;
	private CompoundEdit compoundEdit;

	// These fields are used to help determine whether the edit is an
	// incremental edit. The offset and length should increase by 1 for
	// each character added or decrease by 1 for each character removed.
	private int lastOffset;
	private int lastLength;

	public CompoundUndoManager(JTextComponent component) {
		this.textComponent = component;
		undoManager = this;
		undoAction = new UndoAction();
		redoAction = new RedoAction();

		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoKeystroke");
		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK), "undoKeystroke");
		component.getActionMap().put("undoKeystroke", undoAction);

		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redoKeystroke");
		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK), "redoKeystroke");
		component.getActionMap().put("redoKeystroke", redoAction);

		component.addPropertyChangeListener("document", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Object oldValue = evt.getOldValue();
				if (oldValue != null && oldValue instanceof Document) { //Remove old
					Document document = (Document) oldValue;
					document.removeUndoableEditListener(undoManager);
				}
				Object newValue = evt.getNewValue();
				if (newValue != null && newValue instanceof Document) { //Remove old
					Document document = (Document) newValue;
					document.addUndoableEditListener(undoManager); //New document
				}
			}
		});
		
		component.getDocument().addUndoableEditListener(this);

		//component.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
	}

	public void reset() {
		int limit = getLimit();
		setLimit(0);
		setLimit(limit);
		compoundEdit = null;
		lastOffset = 0;
		lastLength = 0;
		undoAction.updateUndoState();
		redoAction.updateRedoState();
	}

	/*
	** Add a DocumentLister before the undo is done so we can position
	** the Caret correctly as each edit is undone.
	 */
	@Override
	public synchronized void undo() {
		textComponent.getDocument().addDocumentListener(this);
		super.undo();
		textComponent.getDocument().removeDocumentListener(this);
	}

	/*
	** Add a DocumentLister before the redo is done so we can position
	** the Caret correctly as each edit is redone.
	 */
	@Override
	public synchronized void redo() {
		textComponent.getDocument().addDocumentListener(this);
		super.redo();
		textComponent.getDocument().removeDocumentListener(this);
	}

	/*
	** Whenever an UndoableEdit happens the edit will either be absorbed
	** by the current compound edit or a new compound edit will be started
	 */
	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		// Start a new compound edit

		if (compoundEdit == null) {
			compoundEdit = startCompoundEdit(e.getEdit());
			return;
		}

		int offsetChange = textComponent.getCaretPosition() - lastOffset;
		int lengthChange = textComponent.getDocument().getLength() - lastLength;

		// Check for an attribute change
		UndoableEdit edit = e.getEdit();
		if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
			AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) e.getEdit();
			if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
				if (offsetChange == 0) {
					compoundEdit.addEdit(e.getEdit());
					return;
				}
			}
		}

		// Check for an incremental edit or backspace.
		// The Change in Caret position and Document length should both be
		// either 1 or -1.
		if (offsetChange == lengthChange
				&& Math.abs(offsetChange) == 1) {
			compoundEdit.addEdit(e.getEdit());
			lastOffset = textComponent.getCaretPosition();
			lastLength = textComponent.getDocument().getLength();
			return;
		}

		// Not incremental edit, end previous edit and start a new one
		compoundEdit.end();
		compoundEdit = startCompoundEdit(e.getEdit());
	}

	/*
	** Each CompoundEdit will store a group of related incremental edits
	** (ie. each character typed or backspaced is an incremental edit)
	 */
	private CompoundEdit startCompoundEdit(UndoableEdit anEdit) {
		// Track Caret and Document information of this compound edit

		lastOffset = textComponent.getCaretPosition();
		lastLength = textComponent.getDocument().getLength();

		// The compound edit is used to store incremental edits
		compoundEdit = new MyCompoundEdit();
		compoundEdit.addEdit(anEdit);

		// The compound edit is added to the UndoManager. All incremental
		// edits stored in the compound edit will be undone/redone at once
		addEdit(compoundEdit);

		undoAction.updateUndoState();
		redoAction.updateRedoState();

		return compoundEdit;
	}

	/*
	 * The Action to Undo changes to the Document.
	 * The state of the Action is managed by the CompoundUndoManager
	 */
	public Action getUndoAction() {
		return undoAction;
	}

	/*
	 * The Action to Redo changes to the Document.
	 * The state of the Action is managed by the CompoundUndoManager
	 */
	public Action getRedoAction() {
		return redoAction;
	}
//
// Implement DocumentListener
//

	/*
	 * Updates to the Document as a result of Undo/Redo will cause the
	 * Caret to be repositioned
	 */
	@Override
	public void insertUpdate(final DocumentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int offset = e.getOffset() + e.getLength();
				offset = Math.min(offset, textComponent.getDocument().getLength());
				textComponent.setCaretPosition(offset);
			}
		});
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		textComponent.setCaretPosition(e.getOffset());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	class MyCompoundEdit extends CompoundEdit {

		@Override
		public boolean isInProgress() {
			// in order for the canUndo() and canRedo() methods to work
			// assume that the compound edit is never in progress
			return false;
		}

		@Override
		public void undo() throws CannotUndoException {
			// End the edit so future edits don't get absorbed by this edit
			if (compoundEdit != null) {
				compoundEdit.end();
			}

			super.undo();

			// Always start a new compound edit after an undo
			compoundEdit = null;
		}
	}

	/*
	 *	Perform the Undo and update the state of the undo/redo Actions
	 */
	class UndoAction extends AbstractAction {

		public UndoAction() {
			putValue(Action.NAME, "Undo");
			putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
			if (Platform.isMac()) {
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK));
			} else {
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
			}
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.undo();
				textComponent.requestFocusInWindow();
			} catch (CannotUndoException ex) {
			}

			updateUndoState();
			redoAction.updateRedoState();
		}

		private void updateUndoState() {
			setEnabled(undoManager.canUndo());
		}
	}

	/*
	 *	Perform the Redo and update the state of the undo/redo Actions
	 */
	class RedoAction extends AbstractAction {

		public RedoAction() {
			putValue(Action.NAME, "Redo");
			putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
			if (Platform.isMac()) {
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
			} else {
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
			}
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo();
				textComponent.requestFocusInWindow();
			} catch (CannotRedoException ex) {
			}

			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState() {
			setEnabled(undoManager.canRedo());
		}
	}
}
