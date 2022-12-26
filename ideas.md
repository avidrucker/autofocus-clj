# Compelling Ideas

- [front-end] [application architecture] Atomic Design:  "Atoms " make
  up  "Molecules ", Molecules make up  "Organisms ", and Organisms
  make up  "Templates ". When application data is 'injected' into
  Templates, this gives us "Pages".
  
- [notebook-document] [improvements] [productivity] Nest code that
  seems "stable" into do-blocks to speed up evaluation cycles... Or
  migrate to working locally in a REPL to use REPL-Driven-Development
  
- [implementation details] [improvements] Use a "t-list" (similar to
  bit-boards for chess/checkers) to represent list item statuses. This
  would actually need to be a tuple of two numbers. The first # would
  represent the list length. The second number would be a trinary
  representation of each item's status. Because a valid AutoFocus list
  always is either empty OR has the first item as "ready" or "done",
  the first item can always be represented as an integer number of 1
  or 2. An example sequence of list states with each list state built
  from the immediately preceding state:
  - (A) An empty list: (0, '())
  - (B) The list above, with its first new item just added: (1, '(1))
  - (C) The list above, with a second item added: (2, '(10))
  - (D) The list above, where the first item was focused on and
  completed: (2, '(21))
  - (E) The list above, where two new items were added: (4, '(2100))
  - (F) The list above, where a review session of "NY" (no, yes) was
    entered: (4, '(2101))

- [implementation details] [state machine] [improvements] Use a t-time
  for the entire app state (example: (assuming a default empty list
  and first time using the application) time starts at 0, then the
  GUI/CLI loads the list for display, and give the user the option to
  add an item, which they do, entering the user into "adding mode"
  where they, at time 1 enter their new item text, which takes them
  back to their new list of size 1 at time 2. Now, the user may choose
  1 of 2 things: to focus on the auto-dotted ready-to-do item, or,
  they may add another item. Each action by the user at each decision
  point represents one "step" of app-time. Assuming the user decides
  to add one more item, they will choose "Add New" (+1 step), and then
  type and submit their new item (+1 step), taking them back to the
  "viewing mode" where they now see they have a list of size 2 at
  time 4. Note that, if the user had decided that they didn't actually
  want to submit their new item after they had started typing their
  new item, they could instead "Cancel" instead of "Submit" their new
  item creation/addition, and this wouldn't change the forward
  progress of time, but it would result in the list not increasing in
  size. 

- [notebook-document] Implement a function that will, at the bottom of the document,
  run code that will append DOM content to a cell/element at the top
  of the document (ie. the first nav widget) because the nav-widget
  can't be rendered until *after* the function has been
  evaluated... Or, you could also statically render 2-3 widgets as
  well 😉
  
  
