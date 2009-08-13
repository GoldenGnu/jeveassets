

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
-Known Issues
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
_KNOWN_ISSUES___________________________________________________________________

-Updating to from Release Candidate 7a or earlier will remove all your settings

________________________________________________________________________________
_CHANGE_LOG_____________________________________________________________________

Release Candidate 8
-Now uses the user's home directory to store data and settings
-Now support proxy servers

Release Candidate 7a
-Fixed price data bug, introduced in RC7


Release Candidate 7
-Ship Loadouts: Fixed bug that showed charges instead of modules
-Fix bug that prevented the price data from being updated
-Updated eveapi library to version 1.0.0
-Updated nikr log library

Release Candidate 6
-Compiled with Java 6, again...
-It's now possible to mark multiple items as BPOs
-Better exit progress

Release Candidate 5
-Now use Java 5, instead of Java 6 (To enable mac compatibility)

Release Candidate 4
-Corrected contact information, in readme file
-Fixed hidden characters being shown in the values tool
-Ship Loadouts now show all ships

Release Candidate 3
-fixed bug that would make some buttons to small
-CSV Export: Added more options
-blueprints can now be marked as BPO or BPC
-Added option to only filter when enter is pressed

Release Candidate 2
-Fixed bug that would make some dialogs to small

Release Candidate 1b
-New column: Type Count (Total count of this type of asset)
-Apocrypha 1.3.1 data update

Release Candidate 1a
-Fixed bug in the save filter dialog

Release Candidate 1
-Fixed values grand total wallet balance
-Fixed bug with filter mode combobox
-Added popup-menu with copy and paste to all text components
-Price column: Now have gray background if the price was set manually
-Meta column: Fixed a display issue with trailing zero...
-Now search clipboard for API user id & key, when adding API Key

BETA 12
-New column: Region
-Offices should now always show the right location
-Fixed minor filtering bug

BETA 11
-Added more regions to the Eve-Central options
-Save filter dialog, now have auto-complete
-The Set Price Dialog now focus the price field when shown
-Made option in API Manger to show/hide character assets
-New columns: TypeID & Volume

BETA 10
-Fixed "Equals" and "Does not equal" modes for number columns
-Asset prices can now be set manually (set price from the table popup-menu)
-Added more info to the statusbar

BETA 9a
-Fixed program update

BETA 9
-New update mechanism for assets and price data
-Price data from Eve-Central can now be updated manually
-Added options for EVE-Central price data
-Fixed a bug that made the program crash, if not connected to the Internet...

BETA 8
-Spelling corrected in the filter modes ComboBox
-Added thousands separator in the count column
-Added price column, and totals to loadout dialog
-Added Above & Below filter modes, for number columns
-Owner column, should now always show the correct owner (fix not confirmed)
-Tried to fix the Location bug, again (fix not confirmed)

BETA 7
-Table pop-up: Add to filter
-Columns fixed (should now filter as intented)
-CSV export

BETA 6a
-Fixed a bug with the shown update time

BETA 6
-Fixed bug that hid new columns
-Now shows when the assets can be updated again

BETA 5
-New columns: Highest buy price and Lowest sell price
-Better support for T3 Ships 
-Minor adjustments to the Materials and Values dialogs...

BETA 4
-Tried to fix the location bug (Fix not confirmed)

BETA 3
-Fixed bug, that make the program crash (Location bug still pressent)

BETA 2
-Fixed bug, that prevented users to enter API Key and updating assets

BETA 1
-First version

________________________________________________________________________________
_FAQ____________________________________________________________________________

Q: Update the program to a new version

A: Simply overwrite all the old files with the new ones.
   You settings and assets will stay untouched

________________________________________________________________________________

Q: jEveAssets crashed/does not work as intended

A: Please send an email to niklaskr@gmail.com and include the following:
   1.) the log.txt file, found in the jEveAsset directory
   2.) Instructions on how to reproduce the bug

________________________________________________________________________________

Q: Volume column display -1/Region column displays nothing

A: You'll need to update your assets before the correct value is displayed

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
_CONTACT________________________________________________________________________

www:
  http://eve.nikr.net/?page=jeveasset

email:
  niklaskr@gmail.com (English or Danish)

Eve-Online forum thread:
  http://www.eveonline.com/ingameboard.asp?a=topic&threadID=1103224

________________________________________________________________________________
________________________________________________________________________________