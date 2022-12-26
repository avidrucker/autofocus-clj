DEPRECATED, please delete this file!

Design Notes FAQ

- Why make this code sketch?

- What exactly *is* AutoFocus?

	- What are other well-known task management systems exist today
      that employ one or more "decision algorithms"?

- What are your specific intentions for this code sketch?

- How do the different design permutations of AutoFocus vary from each
  other (listed ideally in order of creation, from oldest to newest)?
  
Why make this?

This "code sketch" is a study to test out different ways of writing in and learning Clojure/programming, such as "domain driven design", "idiomatic Clojure", and project-based exploratory/Socratic learning (learning by doing *and* asking questions). Bonus: Add a "learn in public" aspect to boost my motivation & a12y[1] ;) Bonus 2: Teach myself how to procedurally create "jump links" to improve large notebook navigation

[1] A12y stands for "accountability".

What exactly *is* AutoFocus?

"AutoFocus" (abbreviated below in this document to "AF") is a task management system made by Mark Forster - See Mark Forster's AutoFocus page on his website. AutoFocus levels up the potential productivity, motivation, and clarity of to-do lists by infusing them with elements/aspects of bullet journals and binary decision trees. As a task management system, AutoFocus may be a similar class of tool to David Allen's Getting Things Done ("GTD") system.

Q: What are other well-known task management systems exist today that employ one or more "decision algorithms"? --> Research task, ask Mark Forster, ask MF's website forum

What are your specific intentions for this code sketch?

As inspired by note taking apps like Notion, as well as at least one of the design iterations of AutoFocus[1], I'd like to implement a version of AutoFocus that uses a "running list" that can be viewed/segmented by creation date[2].

[1] There appear to be several versions of AutoFocus, such as "AutoFocus Final Version Perfected", "Fast FVP", "AutoFocus Original" (also called "AutoFocus 1").

[2] The default "list view" is one "page" of to-do items per day, though multiple pages per day is another valid approach, with a sensible X number of items per page cut-off (such as something like 10, 15, or 20).

Bonus Question: How do the different design permutations of AutoFocus vary from each other (listed ideally in order of creation, from oldest to newest)?

What have I learned?

Hashmaps are not necessarily easy for low-level Clojurists / LISPers to create sequential structures out of... or maybe they are, and I just missed the memo 😅

Lists are *especially* immutable, and do not encourage index item "mutations"/"swaps" such as assoc, see StackOverflow post "Easy way to change specific list item in list"

Clojure lists are, under the hood, "linked lists" (the data structure)

there isn't efficient random access

In Clojure, if one is to be often modifying* a collection
(*ie. updating/replacing collection elements, even if the collection
itself is to be immutable) where order/sequencing matters, is it
better to use a vector than a list.

Current Issues To Resolve

[x] Convert list API primary data structure to vector since reads are done very often, if as needed, later lists can be used for performance increases as needed for adding of new items [IN-PROGRESS]

Rationale: "Vectors have O(1) random access times, but they have to be pre-allocated. Lists can be dynamically extended, but accessing a random element is O(n)." ~ SO user Svante Source: https://stackoverflow.com/questions/1147975/in-clojure-when-should-i-use-a-vector-over-a-list-and-the-other-way-around

Note: Rendering vectors into Hiccup may require conversion into lists, or it just may be easier on my brain...

[ ] TODO: Confirm whether this is a requirement, a good idea, or neither.

[x] Establish whether you will stay with standard comp sci count (starting at index 0), or starting at 1, for the code --> D5: I will refactor to keep a starting index of 0 for all counts, using a starting count of 1 for only non-technical user-facing display values

[o] Create ~~an item namespace~~, a list namespace, demo data namespace to increase codebase clarity, organization, discoverability / navigability / readability

Note: I'm currently using the following namespaces:

maria.user - This namespace is primarily for rendering Hiccup elements, this can be effectively considered the "view V in MVC" namespace

af.list - This namespace is essentially the "core" of AutoFocus at the moment, one half of the "model M in MVC"

[ ] TODO: implement namespace af.core to encapsulate the application state via a finite state machine, this will be the "controller C in MVC"

af.data - This namespace is essentially one big hashmap of all the domain data as well as the semantic symbols used for rendering, as well as the "app" informational architecture, this can be considered the remaining half of hte "model M in MVC"

af.demo - This namespace stores the demo data to construct dummy items, lists, and application state machine instances

af.test - This namespace is what will, hopefully, be the start of a decent stab at ClojureScript application testing (unit, integration, end-to-end)

[ ] TODO: convert the above namespace descriptions to be comments in each namespace, and/or a single map at the top of this document, or both

[ ] Tidy up code study tasks in their order of creation & code dependencies (pre-reqs/deps go to the top, code that relies on pre-reqs/deps goes below) (+ code readability) [readability]

[ ] Remove global references from code (- bug occurrence frequency, + code quality, + code readability) [quality] [readability]

[ ] Implement code tasks (ala github issues) as data [IN-PROGRESS]

[ ] Implement user stories collection / test tasks as data [IN-PROGRESS]

[o] Add anchor tags / jump links to help navigate document more quickly [IN-PROGRESS]

[x] To page bottom (brittle, but it works)

[x] To page top (chef's kiss!)

[o] To specific Hiccup rendered headings throughout the code [IN-PROGRESS]

Note: This was much easier in Jupyter Notebooks where I could simply use Markdown headings and jump links 😅

[ ] TODO: Add example JN document w/ jump links example

[ ] Move all questions throughout the document to the questions area,
and link from there to the relevant code cell [IN-PROGRESS]

- [ ] TODO: relocate all todo's to top of document, convert to hashmap data dev-tasks or user-stories as appropriate

DONE: Establish domain model constants, save design decisions as data

[x] create explanation hashmap, instead of saving useful semantic infos as comments, so you can add these infos to the in-app 'help section'

[x] Create initial data values

[x] Create an "informal spec" to describe the data shapes in AutoFocus

[x] Import in text prompts from earlier AF design iteration, then save in DOMAIN-data hashmap... DONE: see af.data/copy-text

Refactor references to data

[x] :marks-xo --> (DOMAIN-data :marks-cli) : (replace all references to :marks-xo to  (DOMAIN-data :marks-cli)

Ideas to Consider

[ ] Implement Malli, Spec, or other data validation as necessary, otherwise, leave data-shapes  "functions"  as-is for documentation purposes  (i.e. make an ITEM-valid? function perhaps?) [nice-to-have] [next-time]

[ ] implement todo-text length validation to prevent/discourage todo-items of too short length from being created e.g. :validation #(>  (count %) 2) [nice-to-have]

[ ] implement todo-length validation to prevent creation of todo-items with no text input e.g. :validation #(not (empty? %)) [core-requirement] because creating no-text items is nonsensical, has a negative utility value, and would most likely lead to a negative user experience
