## Compiled Docs

There are three important inputs to the docs.

1) Content files (markdown)

2) src files (java) that hold the compiled code.

3) output of running snippets in the src files. E.G. `table.first(10)`


### To update the Docs

1) Navigate to the  `./docs` sub directory (Where POM is located).

2) Runs the src files and save the output snippets to `output/`

    `mvn compile && mvn exec:java`

3) Inject snippets into content files and copy everything the content to `dist/`

    `mvn com.github.ryancerf:choss-maven-plugin:build-docs`
    
4) Deploy the docs from `dist/`
    
 ### Notes
 
 For more background on injecting snippets see:
 https://github.com/ryancerf/choss-maven-plugin
