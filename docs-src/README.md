## Compiled Docs

There are several pieces that go into the docs.

Note: Paths are all relative to the root of the project.

1) Content files (markdown) files in `docs-src/main/`. These contain the content and links to code snippets defined in
java src files.

2) src files (java) that hold the compiled code (can be anywhere in the project, but are generally in `docs-src/src/`)

3) the output of running snippets in the src files. E.G. the output of running `table.first(10)`

4) The built docs that have not necessarily released are in `docs-src/dist/`

5) The released/deployed docs are in `docs/` at the root of the project. (Github automatically serves whatever is
in the `docs/` directory in the master branch)


### To update the Docs

1) Navigate to the  `./docs-src` sub directory (Where POM is located).

2) Run `mvn compile && mvn exec:java`

* This will run the src files and save the output snippets to `docs-src/output/`

3) Run `mvn com.github.ryancerf:choss-maven-plugin:build-docs`

This will inject snippets into content files and copy everything the content to `docs-src/dist/`
    
4) To deploy the docs run the shell script `./deploy_docs.sh`.

* This will copy the `docs-src/dist/` directory to `docs/`
* Having a staging location for the built docs allows us to choose when to release them.
  We can now work on the docs for a new feature and then release them at the same time the
  feature is released.
    
 ### Notes
 For more background on injecting snippets see:
 https://github.com/ryancerf/choss-maven-plugin
