/*
 * Copyright 2009-2023 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
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

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.data.settings.Colors;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;


public class JFreeChartUtil {

	private static final DateFormat dateFormat = new SimpleDateFormat(Formatter.COLUMN_DATE, new Locale("en"));
	private static final DateFormat yearsFormat = new SimpleDateFormat("yyyy", new Locale("en"));
	private static final DateFormat yearFormat = new SimpleDateFormat("yyyy-MM", new Locale("en"));
	private static final DateFormat monthsFormat = new SimpleDateFormat("MMM", new Locale("en"));
	private static final DateFormat monthFormat = new SimpleDateFormat("MMM dd", new Locale("en"));
	private static final DateFormat daysFormat = new SimpleDateFormat("EEE", new Locale("en"));
	private static final DateTickUnit yearTick = new DateTickUnit(DateTickUnitType.YEAR, 1);
	private static final DateTickUnit monthTick = new DateTickUnit(DateTickUnitType.MONTH, 1);
	private static final DateTickUnit daysTick = new DateTickUnit(DateTickUnitType.DAY, 1);
	private static Font font = null;

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	private JFreeChartUtil() { }

	private static Font getFont() {
		if (font == null) {
			font = new JLabel().getFont();
		}
		return font;
	}

	public static DateAxis createDateAxis() {
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(JFreeChartUtil.getDateFormat());
		dateAxis.setVerticalTickLabels(true);
		dateAxis.setAutoTickUnitSelection(true);
		dateAxis.setAutoRange(true);
		dateAxis.setTickLabelFont(getFont());
		dateAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		return dateAxis;
	}

	public static LogarithmicAxis createLogarithmicAxis(boolean includeZero) {
		LogarithmicAxis logarithmicAxis = new LogarithmicAxis("");
		logarithmicAxis.setStrictValuesFlag(false);
		logarithmicAxis.setNumberFormatOverride(Formatter.AUTO_FORMAT);
		logarithmicAxis.setTickLabelFont(getFont());
		logarithmicAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		logarithmicAxis.setAutoRangeIncludesZero(includeZero);
		return logarithmicAxis;
	}

	public static NumberAxis createNumberAxis(boolean includeZero) {
		NumberAxis numberAxis = new NumberAxis();
		numberAxis.setAutoRange(true);
		numberAxis.setNumberFormatOverride(Formatter.AUTO_FORMAT);
		numberAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		numberAxis.setTickLabelFont(getFont());
		numberAxis.setTickLabelPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		numberAxis.setAutoRangeIncludesZero(includeZero);
		return numberAxis;
	}

	public static XYPlot createPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
		XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
		plot.setBackgroundPaint(Colors.TEXTFIELD_BACKGROUND.getColor());
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainCrosshairLockedOnData(true);
		plot.setDomainCrosshairStroke(new BasicStroke(1));
		plot.setDomainCrosshairPaint(Color.BLACK);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairLockedOnData(true);
		plot.setRangeCrosshairVisible(false);
		return plot;
	}

	public static JFreeChart createChart(Plot plot) {
		JFreeChart jFreeChart = new JFreeChart(plot);
		jFreeChart.setAntiAlias(true);
		jFreeChart.setBackgroundPaint(Colors.COMPONENT_BACKGROUND.getColor());
		jFreeChart.addProgressListener(null);
		jFreeChart.getLegend().setItemFont(getFont());
		jFreeChart.getLegend().setItemPaint(Colors.TEXTFIELD_FOREGROUND.getColor());
		jFreeChart.getLegend().setBackgroundPaint(Colors.COMPONENT_BACKGROUND.getColor());
		return jFreeChart;
		//jFreeChart.setTextAntiAlias(false);
		//jFreeChart.setAntiAlias(false);
		/*
		Map<RenderingHints.Key,Object> rh = new HashMap<>();
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		//rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);


		//rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		//rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		//rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
		//rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
		//rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		//rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		//rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		//rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		//rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		//rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
		//rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

		//rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		//rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

		//rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		//rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		jFreeChart.setRenderingHints(new RenderingHints(rh));
		*/
	}

	public static ChartPanel createChartPanel(JFreeChart jChart) {
		ChartPanel jChartPanel = new ChartPanel(jChart);
		InstantToolTip.install(jChartPanel);
		jChartPanel.setDomainZoomable(false);
		jChartPanel.setRangeZoomable(false);
		jChartPanel.setPopupMenu(null);
		//jChartPanel.addChartMouseListener(listener);
		jChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
		jChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
		jChartPanel.setMinimumDrawWidth(10);
		jChartPanel.setMinimumDrawHeight(10);
		return jChartPanel;
	}

	public static XYLineAndShapeRenderer createRenderer() {
		return new SimpleRenderer();
	}

	public static void updateTickScale(DateAxis domainAxis, NumberAxis numberAxis, TimePeriodValuesCollection dataset) {
		updateTickScale(domainAxis);
		updateTickScale(numberAxis, dataset);
	}

	public static void updateTickScale(DateAxis domainAxis, NumberAxis numberAxis, Number maxNumber) {
		updateTickScale(domainAxis);
		updateTickScale(numberAxis, maxNumber);
	}

	public static void updateTickScale(NumberAxis numberAxis, TimePeriodValuesCollection dataset) {
		updateTickScale(numberAxis, DatasetUtils.findMaximumRangeValue(dataset));
	}

	public static void updateTickScale(NumberAxis numberAxis, Number maxNumber) {
		if (maxNumber != null && maxNumber instanceof Double) {
			double max = (Double) maxNumber;
			if (max >     1_000_000_000_000.0) {//Higher than 1 Trillion
				numberAxis.setNumberFormatOverride(Formatter.TRILLIONS_FORMAT);
			} else if (max > 1_000_000_000.0) {	//Higher than 1 Billion
				numberAxis.setNumberFormatOverride(Formatter.BILLIONS_FORMAT);
			} else if (max >     1_000_000.0) {	//Higher than 1 Million
				numberAxis.setNumberFormatOverride(Formatter.MILLIONS_FORMAT);
			} else {							//Default
				numberAxis.setNumberFormatOverride(Formatter.LONG_FORMAT);
			}
		}
	}

	public static void updateTickScale(DateAxis domainAxis) {
		Date maximumDate = domainAxis.getMaximumDate();
		Date minimumDate = domainAxis.getMinimumDate();
		long diff = Math.abs(maximumDate.getTime() - minimumDate.getTime());
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if (days > 732) {		 // above 2 years
			domainAxis.setTickUnit(yearTick, false, true);
			domainAxis.setDateFormatOverride(yearsFormat);
		} else if (days > 400) { // above 1 years
			domainAxis.setTickUnit(monthTick, false, true);
			domainAxis.setDateFormatOverride(yearFormat);
		} else if (days > 60) {  // above 2 months
			domainAxis.setTickUnit(monthTick, false, true);
			domainAxis.setDateFormatOverride(monthsFormat);
		} else if (days > 7) {  // above 7 days
			domainAxis.setAutoTickUnitSelection(true, false); //Auto (hard zone to do well)
			domainAxis.setDateFormatOverride(monthFormat);
		} else {				// bellow 7 days
			domainAxis.setTickUnit(daysTick, false, true);
			domainAxis.setDateFormatOverride(daysFormat);
		}
	}

	public static class SimpleRenderer extends XYLineAndShapeRenderer {

		private final Stroke LEGEND_STROKE = new BasicStroke(0.9f);
		private final Shape LEGEND_SHAPE = new Ellipse2D.Float(-5.5f, -5.5f, 11f, 11f);
		private final Shape ITEM_SHAPE = new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f);


		public SimpleRenderer(boolean lines, boolean shapes) {
			super(lines, shapes);
		}

		public SimpleRenderer() {
			super(true, false);
		}

		@Override
		public LegendItem getLegendItem(int datasetIndex, int series) {
			LegendItem original = super.getLegendItem(datasetIndex, series); //To change body of generated methods, choose Tools | Templates.
			LegendItem fixed = new LegendItem(
				original.getLabel(),
				original.getDescription(),
				original.getToolTipText(),
				"", //urlText
				LEGEND_SHAPE,
				original.getLinePaint(), //set fill paint to line paint
				LEGEND_STROKE, //original.getOutlineStroke(),
				Color.BLACK
			);
			fixed.setSeriesIndex(series);
			fixed.setDatasetIndex(datasetIndex);
			return fixed;
		}

		@Override
		public Shape getItemShape(int row, int column) {
			return ITEM_SHAPE;
		}
	}
}
