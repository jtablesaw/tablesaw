## Introduction

Tablesaw provides excellent functionality for easy and efficient manipulation, vizualization, and exploration of data from a variety of sources. A natural extension of this is the ability to utilize statistical/machine learning methods alongside this functionality. Tablesaw now supports basic integration with the leading JVM machine learning library, [Smile](https://haifengl.github.io/). 

Smile supports numerous machine learning techniques, everything from basic linear regression to unsupervised learning algorithms. The library boasts top of the line performance both in the JVM realm and in comparison to alternatives in the Python/R ecosystems. 

At the basic level, one can use Tablesaw to do all of the data manipulation required for a project and then easily convert a Table to the Smile DataFrame format when passing it off to a model. 

```Java
Table data = Table.read().csv("path/to/file.csv");

//clean, manipulate, visualize data as needed

//convert to Smile Dataframe format
DataFrame data_smile = data.smile().toDataFrame();
```

The Tablesaw User Guide contains several examples of how to use Tablesaw and Smile together to implement popular machine learning methods. To add Smile to your project, add the dependency below to your Gradle or Maven file. **Though there are newer versions available, Smile 2.0.0 was used in the development of the tutorials and is recommended.**


```Java
//Gradle
// https://mvnrepository.com/artifact/com.github.haifengl/smile-core
    implementation group: 'com.github.haifengl', name: 'smile-core', version: '2.0.0'
```

```Java
//Maven
<dependency>
  <groupId>com.github.haifengl</groupId>
  <artifactId>smile-core</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Tutorials

* [Linear Regression with Smile and Tablesaw: Moneyball Tutorial](https://jtablesaw.github.io/tablesaw/userguide/ml/Moneyball Linear regression)
* [K-Means clustering with Smile and Tablesaw: NYC Uber Tutorial](https://jtablesaw.github.io/tablesaw/userguide/ml/Uber K Means)
* [Classification using Random Forests with Smile and Tablesaw: Heart Tutorial](https://jtablesaw.github.io/tablesaw/userguide/ml/Heart Random forest)
