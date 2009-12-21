

               ########             #####   
               ########            #######  
               ###                ###   ### 
             # ######## #  # #### ######### #### #### #### ##### ####
               ######## #  # #    ######### #    #    #      #   #
             # ###      #  # #### ###   ### #### #### ####   #   ####
             # ######## #### #    ###   ###    #    # #      #      #
             # ########  ##  #### ###   ### #### #### ####   #   ####
             #
           ###         #### ####  ##  ###  ## ## ####
                       #  # #    #  # #  # # # # #
                       #### #### #### #  # #   # ####
                       # #  #    #  # #  # #   # #
                       #  # #### #  # ###  #   # ####
________________________________________________________________________________
_INDEX__________________________________________________________________________

-About
-Requierments
-Run
-Change log
-FAQ
-Contact

________________________________________________________________________________
_ABOUT__________________________________________________________________________

jEveAssets is an out-of-game asset manager for Eve-Online, writen in Java

________________________________________________________________________________
_REQUIREMENTS___________________________________________________________________

-Java Runtime Environment 6 (Get it free at: http://java.com/)
-An Eve-Online account (to have any use of the program)

________________________________________________________________________________
_RUN____________________________________________________________________________

Run on Windows:
-Double click on jEveAssets.jar

Run on Linux:
-in terminal run "java -jar /path/to/jeveassets/jEveAssets.jar" 
or
-cd to jEveAssets directory and run "java -jar jEveAssets.jar"

________________________________________________________________________________
_CHANGE_LOG_____________________________________________________________________

1.2.1
Bug fixes:
-Fix bug that would sometimes hide industry jobs and market orders
-Fixed bug that would crash jEveAssets when updating assets (holiday gift bug)

New Features:
-Now backup settings files (on save)

Known Issues:
-The holiday gift show up as !XXXX

1.2.0
Bug fixes:
-JTextField swing bug (workaround)
-New assets have no price (from Market Orders/Industry Jobs)
-Conquerable Stations locations get error string

New Features:
-Added market orders tool
-Added industry jobs tool
-Now retain window position/size on restart
-Added a label to the toolbar that show the current filter
-Now show the eve server time on the statusbar
-The security column now have the filter modes: "Great than" and "Less than"
-Price data from both eve-metrics and eve-central (Candles pricing library)
-Added more options to the API Manager
-The table now save the selection on update
-Added the ability highlight the selected row
-The price field in price settings, now have focus when adding a new price


1.1.0
-Fixed bug with invalid proxy settings
-Fixed bug with pos and industry jobs
-Added hide/show columns to the main menu
-Fixed bug in save filter dialog
-Better error messages when Updating assets
-New layout for about dialog
-The default eve-central price can now be changed
-Added new filter modes: Greater/Less then column (compare two columns)
-API keys can now be changed after they have been added
-Filtering is now only triggered when no keys have been pressed for 500ms
-Now automatically mark blueprints that have been used as copy/original 
-All settings are now in the same dialog
-Improved the way progress is showed when updating assets and price data
-New dialog key bindings: Escape cancel and enter saves
-Industry jobs: Now adds blueprints in use to the asset list
-Market orders: now adds remaining items from sell orders to the assets list
-Portable setting: save all files in program directory (see FAQ)
-New Column: Reprocessed value
-New Column: System security status
-Added volume to statusbar and table popup menu

1.0.0
-Fixed minor bug with CSV & Fittings export

1.0.0 Release Candidate 8
-Now uses the user's home directory to store data and settings
-Now support proxy servers

1.0.0 Release Candidate 7a
-Fixed price data bug, introduced in RC7


1.0.0 Release Candidate 7
-Ship Loadouts: Fixed bug that showed charges instead of modules
-Fix bug that prevented the price data from being updated
-Updated eveapi library to version 1.0.0
-Updated nikr log library

1.0.0 Release Candidate 6
-Compiled with Java 6, again...
-It's now possible to mark multiple items as BPOs
-Better exit progress

1.0.0 Release Candidate 5
-Now use Java 5, instead of Java 6 (To enable mac compatibility)

1.0.0 Release Candidate 4
-Corrected contact information, in readme file
-Fixed hidden characters being shown in the values tool
-Ship Loadouts now show all ships

1.0.0 Release Candidate 3
-fixed bug that would make some buttons to small
-CSV Export: Added more options
-blueprints can now be marked as BPO or BPC
-Added option to only filter when enter is pressed

1.0.0 Release Candidate 2
-Fixed bug that would make some dialogs to small

1.0.0 Release Candidate 1b
-New column: Type Count (Total count of this type of asset)
-Apocrypha 1.3.1 data update

1.0.0 Release Candidate 1a
-Fixed bug in the save filter dialog

1.0.0 Release Candidate 1
-Fixed values grand total wallet balance
-Fixed bug with filter mode combobox
-Added popup-menu with copy and paste to all text components
-Price column: Now have gray background if the price was set manually
-Meta column: Fixed a display issue with trailing zero...
-Now search clipboard for API user id & key, when adding API Key

1.0.0 BETA 12
-New column: Region
-Offices should now always show the right location
-Fixed minor filtering bug

1.0.0 BETA 11
-Added more regions to the Eve-Central options
-Save filter dialog, now have auto-complete
-The Set Price Dialog now focus the price field when shown
-Made option in API Manger to show/hide character assets
-New columns: TypeID & Volume

1.0.0 BETA 10
-Fixed "Equals" and "Does not equal" modes for number columns
-Asset prices can now be set manually (set price from the table popup-menu)
-Added more info to the statusbar

1.0.0 BETA 9a
-Fixed program update

1.0.0 BETA 9
-New update mechanism for assets and price data
-Price data from Eve-Central can now be updated manually
-Added options for EVE-Central price data
-Fixed a bug that made the program crash, if not connected to the Internet...

1.0.0 BETA 8
-Spelling corrected in the filter modes ComboBox
-Added thousands separator in the count column
-Added price column, and totals to loadout dialog
-Added Above & Below filter modes, for number columns
-Owner column, should now always show the correct owner (fix not confirmed)
-Tried to fix the Location bug, again (fix not confirmed)

1.0.0 BETA 7
-Table pop-up: Add to filter
-Columns fixed (should now filter as intented)
-CSV export

1.0.0 BETA 6a
-Fixed a bug with the shown update time

1.0.0 BETA 6
-Fixed bug that hid new columns
-Now shows when the assets can be updated again

1.0.0 BETA 5
-New columns: Highest buy price and Lowest sell price
-Better support for T3 Ships 
-Minor adjustments to the Materials and Values dialogs...

1.0.0 BETA 4
-Tried to fix the location bug (Fix not confirmed)

1.0.0 BETA 3
-Fixed bug, that make the program crash (Location bug still pressent)

1.0.0 BETA 2
-Fixed bug, that prevented users to enter API Key and updating assets

1.0.0 BETA 1
-First version

________________________________________________________________________________
_FAQ____________________________________________________________________________

Q: Update the program to a new version

A: Simply overwrite all the old files with the new ones.
   You settings and assets will stay untouched

________________________________________________________________________________

Q: jEveAssets crashed/does not work as intended

A: Please send an email to niklaskr@gmail.com and include the following:
   1.) The latest error.txt in the logs directory
   2.) Instructions on how to reproduce the bug

________________________________________________________________________________

Q: I can't sort columns

A: You need to double click to sort a new column
   just clicking will, sup-sort the column

________________________________________________________________________________

Q: How do I manually set price for asset

A: Right click the asset you want to set price for in the table
   and select "Set price..." in the popup-menu

________________________________________________________________________________

Q: How do I mark a blueprint as an original

A: Right click the asset you want to mark as BPO for in the table
   and select "Blueprint Original" in the popup-menu

________________________________________________________________________________

Q: The [...] library is missing

A: Please re-download jEveAssets from http://eve.nikr.net/?page=jeveasset
   And leave the unzipped folder intact 
________________________________________________________________________________

Q: How do I use portable settings?

A: Add the command line argument: -portable

________________________________________________________________________________

Q: Where is the user files?

A:

   Windows XP:
   C:\Documents and Settings\[USERNAME]\.jeveassets\

   Windows Vista:
   C:\Users\[USERNAME]\.jeveassets\

   Linux:
   Send me an email if you know (niklaskr@gmail.com)

   Mac:
   Send me an email if you know (niklaskr@gmail.com)

   Replace [USERNAME] with your username

________________________________________________________________________________
_CONTACT________________________________________________________________________

www:
  http://eve.nikr.net/?page=jeveasset

email:
  niklaskr@gmail.com (English or Danish)

Eve-Online forum thread:
  http://www.eveonline.com/ingameboard.asp?a=topic&threadID=1103224

________________________________________________________________________________
________________________________________________________________________________