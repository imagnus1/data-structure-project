CSC 212 - RIDE-SHARING PROJECT

Team responsibilities (original split retained)
A: Osamah Alsheddi - 443102410 - custom lists and list operations.
B: Ibrahim Mohammed Aljebreen - 445100804 - system rules and CSV loading.
C: Mohammed Alsamari - 445100816 - models, menu, report assembly.
Everyone should understand the full application flow and rehearse the demo.

CONTENTS
*.java: complete application source, including unchanged supplied interfaces.
tests/ProjectTest.java: integration tests using the real data structures.
sample-data/: small synthetic examples, NOT official assignment datasets.
Report.pdf: cover, implementation description, and complexity analysis.
StudyGuide.pdf: simple A/B/C walkthrough and professor-demo preparation.
VALIDATION.txt: checks executed and remaining limitations.

REQUIREMENTS
A Java JDK (version 8 or newer). A JDK includes both javac and java.
Run commands from this folder so relative CSV paths resolve correctly.
No external libraries are needed.

RUN WITH OFFICIAL DATA
Put the supplied riders_100.csv, drivers_30.csv, and rides_40.csv in this
folder. Then double-click run.bat or run:
    javac -encoding UTF-8 -d build *.java
    java -cp build Main
Main loads riders, then drivers, then rides before displaying the menu.
If the official files have different names, pass three file paths:
    java -cp build Main "riders.csv" "drivers.csv" "rides.csv"
Missing/invalid files produce load messages, and the menu still opens.

RUN THE INCLUDED PRACTICE DATA
    run.bat --demo
Or after compilation:
    java -cp build Main --demo
This uses 5 example riders, 4 drivers, and 4 rides from sample-data/.
It does not rename synthetic data to pretend it is the official dataset.

RUN THE CHECKS
    test.bat
Or:
    javac -encoding UTF-8 -d build *.java tests/ProjectTest.java
    java -cp build ProjectTest
The Java test exits normally on success and throws an error on failure.
test.bat prints the success message.

IMPORTANT BEHAVIOR
Rider/driver main storage is sorted by ID; a second linked-list index is
sorted by name, allowing O(n) alphabetical listing without re-sorting.
Rides are kept sorted by pickup location. String matching is exact and
case-sensitive. No built-in Java collections are used.
Shared rides need at least two distinct riders when first scheduled.
Back-to-back time ranges are allowed; overlapping ranges are rejected.
Removing people through RideSharingSystem also cleans associated histories.
CSV loaders keep valid rows and return false if any row is rejected.
Two shared rides may have the same pickup location; the participants query
returns the distinct riders from all matching shared rides.
The UI exposes the exact required 12 options; removals are public system
methods exercised in tests, since the required menu has no removal option.

DESIGN BOUNDARY
The application changes records through RideSharingSystem. List snapshots
copy list nodes, not the model objects. Model setters remain available
because the supplied interfaces require them. Directly changing a stored
name, email, plate, pickup location, driver, participant list, or ride history
outside the system can invalidate indexes or rules. There is no edit menu.
If an edit feature is added later, it must validate and update the indexes
and histories together. This version schedules through the system only.

BEFORE SUBMISSION
1. Add the official CSV files; they were not in the supplied download.
2. Run the default startup with those files and verify the load messages.
3. Check the report cover names/IDs and responsibility split.
4. Read StudyGuide.pdf and practice explaining code instead of memorizing.
5. Upload the source/report according to your instructor's submission rules.
The project ZIP already contains the report; upload it separately as well
if the LMS requests a separate PDF. Do not include a Java JDK or build files.

GITHUB
Use this complete folder as the updated project content, replacing the
earlier Role B draft. Keep DateTime.java with this exact spelling. Remove
the old Datetime.java from the repository if GitHub still lists it, and
exclude all old temporary-stubs. Preserve all supplied interface files.
On Windows, a case-only rename may need a temporary filename in Git first.
