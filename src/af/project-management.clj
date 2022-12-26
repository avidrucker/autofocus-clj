(ns af.pm)
;; project management

; Q: Why do single segement namespaces trigger some sort of warning/error  (message to the right in Maria Dot Cloud)?
;; A: Single segment namespaces are considered a  "code smell" and/or  "poor code quality" item in Clojure(Script). This is not a unique feature to Maria Dot Cloud. ~ Avi. TODO: Verify this answer.

(def core-user-stories
  ;; TODO: Riff on idea of  "user-cants" where user limitations, restraints, and lacks of capacity are helpful to clearly illustrate what is and isn't doable/possible by the user, for example: User cannot create items with only whitepsace or no alpha-numeric characters. 
  "Notes: TBD == 'To be determined'"
  [{:id 1 :text "User can create a new todo item with their own custom text input."}
   {:id 2 :text "User can see their list rendered out w/ each item's status rendered as a mark."}
   ;; TODO: Import into AutoFocus sketch explanation of how AutoFocus works for those who are curious to read about it w/ a link to Mark Forster's website to learn more
   ;; TODO: Import into AutoFocus sketch some copy-text for  "About AutoFocus" 
   {:id 3 :text "User can learn what AutoFocus is, how it works, and how it came to be, via an 'About' section of the program."}])

(def bonus-user-stories 
  [{:id 1 :text "The user can toggle via a toggle button, setting, or otherwise, to display or hide items that have been marked as 'done'."}])

(def bugs
  [{:id 1
    :text "As of D31, prev and next nav-links move to slightly off of the main window of Maria Dot Cloud... This is likely due to the absolute positioned 'floating' header. Proposed solutions include adding margins/padding above the widgets (a reasonable idea for now) and rendering 'blank' cells (a not-so-great idea)."
    :status :done
    :resolution "The mostly successful resolution implementation for this bug was to add 2em of padding-top to the h2 headings. A few headings remain 'hard to target with precision.'"
    :next-steps "Create a research ticket to investigate further strategies such as minimum height nav widgets, fixed height nav widgets, and/or, rather than targetting the headings, to instead target the entire nav widget 'molecule'."}])

(def dev-tasks
  "This list of items is akin to tickets/issues in a project management system."
  [{:id 1
    :text "Dev encodes the AF ruleset as a hashmap of keywords and strings. This should be the primary Single Source of Truth ('SSoT'), so that the dev can refer to this, and so can all interested parties: e.g. the designer, the user, etc.. Rationale: Since this is a one-person project, and organization/tidyness of info is paramount to its smooth/sustained progress, keeping important data in a singular place is ideal."
    :status :done}
   {:id 2
    :text "Dev replaces current implementation of hashmap with vector for base todo list structure."
    :status :done}
   {:id 3 
    :text "Dev extracts questions, TODOs, and other notes from design-decisions, initial-defaults, and other 'just data' hashmaps."
    :status :ready}])

(def dev-tasks-string-bag
  ;; TODO: convert the following to dev-tasks data in the hashmap above
  "- [x] 010 Make function to create new items from task text (required) and status (optional)
- [ ] 011 Test create-new-item function w/ only task text
- [ ] render a todo as it would be in a list
- [ ] give a short tutorial in-app to describe how AutoFocus works (high level overview, the 3 item statuses (new, ready, done), the 3 actions (add, review, focus), and what are the trade-offs between paper AutoFocus and digital AutoFocus)
- [ ] implement clone-item function (takes in an item, and 'duplicates it' by taking the original's text only, and giving the new (clone) item a new index, a status of 'new', and, *maybe* (i.e. decision TBD) a Boolean flag of ':is-clone' or perhaps an Integer ':clone-number' to indicate how many times this item has been cloned)")





