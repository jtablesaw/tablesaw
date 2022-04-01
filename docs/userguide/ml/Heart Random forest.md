# Random Forest with Smile & Tablesaw

While linear regression analysis (introduced in the <a href="https://jtablesaw.github.io/tablesaw/userguide/ml/Moneyball%20Linear%20regression">Moneyball tutorial</a>) is widely used and works well for a variety of problems, tree-based models provide excellent results and be applied to datasets with both numerical and categorical features, without making any assumption of linearity. In addition, tree based methods can be used effectively for regression and classification tasks. 
 
This tutorial is based on Chapter 8 (Tree Based Methods) of the widely used and freely available textbook <a href="https://www.statlearning.com/">An Introduction to Statistical Learning, Second Edition</a>.

### Basic Decision Trees

The basic idea of a decision tree is simple: use a defined, algorithmic approach to partition the feature space of a dataset into regions in order to make a prediction. For a regression task, this prediction is simply the mean of the response variable for all training observations in the region. **In any decision tree, every datapoint that falls within the same region receives the same prediction.**

The following figure illistrates a two-dimensional feature space that has been partitioned according to a greedy algorithm that minimizes the residual sum of squares. 

<p align="center">
<img src="https://github.com/jbsooter/tablesaw/blob/6856ae9e92648ad1dfbb62f1ff09e744eebe4154/docs/userguide/images/ml/random_forest/Decision_Tree_8.1.jpg" width="300" height = "300"><img src="https://github.com/jbsooter/tablesaw/blob/6856ae9e92648ad1dfbb62f1ff09e744eebe4154/docs/userguide/images/ml/random_forest/Decision_Tree_8.2.jpg" width="300" height = "300">
</p>

<p align="center">
<sub>Credit: An Introduction to Statistical Learning, Second Edition Figures 8.1 and 8.2</sub>
</p>

As you can see, the feature space is split into three regions, and three predictions are possible: 5.00, 6.00, and 6.74. 

While decision trees are easily interpretable, especially when looking at the visual of the tree on the left, basic decision trees like this one are generally less accurate than other regression/classification approaches. More advanced tree based methods, however, not only compete with other approaches, but often provide exceptional performance. 

### Random Forest

Random Forest is an **ensemble method** that builds on basic decision trees to form a model that is the composition of a large number of individual decision trees. In a Random Forest model, *n* decision trees are grown independently, with each tree considering only a subset of of predictors *m*, using only a subset of the training data. Becuase the majority of the predictors are not considered in each individual tree, the predicitons of each individual tree are decorrelated and therefore the average decision of all the trees in the model will have lower variance and be of greater predictive value. The greedy algorithm used to construct decision trees is heavily influenced by strong predictors, so by excluding a number of predictors when each tree in the Random Forest is grown, the model can explore the predictive value of other features that previously may have been largely ignored. 

### Implementing Random Forest for the Heart dataset using Tablesaw + Smile

The Heart dataset contains 13 qualitative and quantitative predictors for 303 patients who sought medical attention due to chest pain. The response represents a binary classification scenario as we want to predict which patients have heart disease and which do not. You can download the dataset <a href="https://github.com/jbsooter/tablesaw/blob/53358464317fbd66f8a50fe240b67765593a7659/data/Heart.csv">here</a>.

As usual, you will need to add the smile, tablesaw-core, and tablesaw-jsplot dependencies. (Described in Moneyball Tutorial)

First, load and clean the data. Qualitative features must be represented as integers to build the model. 

```
Table data = Table.read().csv("Heart.csv");

//encode qualitative features as integers to prepare for Smile model. 
data.replaceColumn("AHD", data.stringColumn("AHD").asDoubleColumn().asIntColumn());
data.replaceColumn("Thal", data.stringColumn("Thal").asDoubleColumn().asIntColumn());
data.replaceColumn("ChestPain", data.stringColumn("ChestPain").asDoubleColumn());

//Remove the index column, as it is not a feature
data.removeColumns("C0");

//Remove rows with missing values
data = data.dropRowsWithMissingValues();

//print out cleaned dataset for inspection
System.out.println(data.printAll());
```

Next, segment your dataset into two distinct tables. Rows are randomly assigned tables by default using the .sampleSplit() function.

```
//Split the data 70% test, 30% train
Table[] splitData = data.sampleSplit(0.7);
Table dataTrain = splitData[0];
Table dataTest = splitData[1];
```

Now build an initial RandomForest model using the training dataset and sensible parameter values. *m*, the number of features to be considered for each tree, is set to the square root of the number of features. *n*, or ntrees, is set to 50, a reasonmable starting point that will run quickly. *d*, or maxDepth, is the maximum depth of any individual decision tree, and is set to another generally accepted sensible value, 7. maxNodes is set to 100 and represents the most number of terminal decision making nodes a tree can have. All of these values are considered **Hyperparameters** and should be refined by the user to improve the accuracy of the model. 

```
//initial model with sensible parameters
RandomForest RFModel1 = smile.classification.RandomForest.fit(
     Formula.lhs("AHD"),
     dataTrain.smile().toDataFrame(),
     50, //n
     (int) Math.sqrt((double) (dataTrain.columnCount() - 1)), //m = sqrt(p)
     SplitRule.GINI,
     7, //d 
     100, //maxNodes
     1,
     1
);
```

View the first decision tree generated by the model. 

```
System.out.println(RFModel1.trees()[0]);
```

Predict the response of the test dataset using your model and assess model accuracy. 

```
//predict the response of test dataset with RFModel1
int[] predictions = RFModel1.predict(dataTest.smile().toDataFrame());

//evaluate % classification accuracy for RFModel1
double accuracy1 = Accuracy.of(dataTest.intColumn("AHD").asIntArray(), predictions);
System.out.println(accuracy1);
```

Generate and plot feature importance. According to the Smile documentation,  the built in .importance() function calculates variable importance as the sum decrease in the Gini impurity criterion across all node splits on that variable, averaged across all trees in the Random Forest. In other words, the more that impurity declines when a node is split on a variable, the more important it is to the predictive power of a model. 

```
//measure variable importance (mean decrease Gini Index)
double[] RFModel1_Importance = RFModel1.importance();
System.out.println(Arrays.toString(RFModel1_Importance));

//plot variable importance with tablesaw
Table varImportance = Table.create("featureImportance");
List<String> featureNames = dataTrain.columnNames();
featureNames.remove(13); //remove response
varImportance.addColumns(DoubleColumn.create("featureImportance", RFModel1_Importance), StringColumn.create("Feature",  featureNames));
varImportance = varImportance.sortDescendingOn("featureImportance");
Plot.show(HorizontalBarPlot.create("Feature Importance", varImportance, "Feature", "featureImportance"));
```

As you can see, features **Fbs** and **ExAng** have the lowest importance in the model, while **Age**, **MaxHR**, **RestBP**, and **Chol** all appear to be of great importance. 

<p align="center">
<img src="https://github.com/jbsooter/tablesaw/blob/c38cc384989f3093469b860e25a193b3445f2673/docs/userguide/images/ml/random_forest/Tablesaw_Feature_Importance.png" width="650" height = "500">
</p>

Another (lesser) concern when selecting features to include in the model is having two features that are highly correlated with one another. At best, including all of such features is redundant, at worst, it could negatively impact model performance. 

Spearman's correlation metric provides a measure of feature correlation and can be generated automatically with Tablesaw. (-1 represents an extreme negative correlation, +1 represents an extreme positive correlation)

**Generate a matrix of Spearman's correlation metrics between all features.**

```
//construct correlation matrix
Table corr = Table.create("Spearman's Correlation Matrix");
corr.addColumns(StringColumn.create("Feature"));
 for(String name: dataTest.columnNames())
 {
      corr.addColumns(DoubleColumn.create(name));
 }
 for(int i = 0; i < 13; i++)
 {
      for(int j = 0; j < 13; j++)
      {
         corr.doubleColumn(i+1).append(dataTrain.numericColumns(i).get(0).asDoubleColumn().spearmans(dataTrain.numericColumns(j).get(0).asDoubleColumn()));
       }
  }
 corr.stringColumn("Feature").addAll(dataTrain.columnNames());

//print correlation matrix
 System.out.println(corr.printAll());
```


**Output:**
```
> Spearman's Correlation Matrix                                                                                                                                                               
  Feature   |          Age           |           Sex           |       ChestPain        |         RestBP         |           Chol           |           Fbs           |        RestECG         |          MaxHR          |         ExAng          |        Oldpeak         |          Slope          |           Ca           |          Thal          |  AHD  |
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
       Age  |                     1  |   -0.06927628444745006  |  -0.22394932648630028  |   0.27229365905249764  |     0.22118481723091846  |    0.14611908612946659  |   0.11406960795529728  |   -0.36545221179207127  |   0.15931863959698744  |   0.28900547968495544  |    0.22260022799577947  |    0.4011001090020789  |   0.07715137522340691  |    ?  |
       Sex  |  -0.06927628444745008  |                      1  |  -0.13316187576213706  |  -0.03057873931318479  |     -0.1628330278069285  |    0.02090713852553248  |   0.09796166778831927  |  -0.006638477572643997  |    0.1169578622816031  |   0.07966109837586906  |   0.010629364675314218  |   0.07985662144788944  |   0.21642454797537491  |    ?  |
 ChestPain  |  -0.22394932648630048  |   -0.13316187576213717  |                     1  |  -0.12198916347829399  |    0.008301827297130165  |    0.04899395671714113  |   -0.1506474706276619  |     0.2985193811915738  |   -0.3450753984803765  |  -0.39302241652825437  |    -0.2922533629231065  |  -0.24053989827556335  |   -0.2201496705625448  |    ?  |
    RestBP  |   0.27229365905249775  |  -0.030578739313184063  |  -0.12198916347829408  |                     1  |       0.175864939760871  |    0.12191018362176585  |   0.15865758446536152  |   0.014555880234402178  |    0.0683177894848273  |    0.1276491654797409  |    0.10645515850387793  |   0.07480644633180983  |   0.05578224491505685  |    ?  |
      Chol  |   0.22118481723091832  |   -0.16283302780692843  |  0.008301827297131362  |   0.17586493976087092  |                       1  |    0.07421884218213347  |   0.16333806160771683  |  -0.003528241240100859  |   0.07301439688556297  |    0.0728916126718947  |  -0.007111306426534922  |   0.16350017346933124  |   0.08929791710179744  |    ?  |
       Fbs  |   0.14611908612946622  |    0.02090713852553275  |  0.048993956717142226  |   0.12191018362176628  |     0.07421884218213366  |                      1  |   0.05513351299882902  |    0.01858564272016514  |  -0.02090713852553275  |  -0.04296529609793933  |   -0.05299486146325138  |   0.16270896156687129  |  -0.08388064854854119  |    ?  |
   RestECG  |   0.11406960795529668  |    0.09796166778831882  |   -0.1506474706276615  |   0.15865758446536202  |     0.16333806160771708  |    0.05513351299882953  |                     1  |   -0.11176602498485706  |   0.10588361725443558  |   0.15884571558825455  |    0.16977739858683863  |    0.1498166003962869  |   0.03815270577052741  |    ?  |
     MaxHR  |   -0.3654522117920714  |  -0.006638477572643291  |    0.2985193811915737  |  0.014555880234398303  |  -0.0035282412401105687  |   0.018585642720163817  |  -0.11176602498485677  |                      1  |   -0.4037706928677693  |  -0.40721013703608494  |    -0.4216911782327234  |   -0.2150929419777653  |   -0.1666763672778152  |    ?  |
     ExAng  |   0.15931863959698725  |    0.11695786228160313  |  -0.34507539848037655  |   0.06831778948482783  |     0.07301439688556298  |  -0.020907138525532488  |   0.10588361725443567  |   -0.40377069286776923  |                     1  |   0.30346676765576025  |    0.29297186386333846  |   0.15372165582348754  |   0.18652000458816637  |    ?  |
   Oldpeak  |   0.28900547968495544  |    0.07966109837586963  |  -0.39302241652825426  |     0.127649165479741  |     0.07289161267189483  |  -0.042965296097940975  |   0.15884571558825442  |   -0.40721013703608494  |   0.30346676765576025  |                     1  |     0.5693233423680345  |   0.20567421671378322  |   0.29388020647671015  |    ?  |
     Slope  |   0.22260022799577947  |   0.010629364675317408  |   -0.2922533629231065  |   0.10645515850387771  |   -0.007111306426533287  |   -0.05299486146325107  |   0.16977739858683863  |    -0.4216911782327234  |    0.2929718638633384  |    0.5693233423680345  |                      1  |   0.04223259186794805  |   0.13133340300885155  |    ?  |
        Ca  |    0.4011001090020789  |     0.0798566214478899  |   -0.2405398982755634  |   0.07480644633181012  |     0.16350017346933138  |    0.16270896156687129  |   0.14981660039628733  |    -0.2150929419777654  |   0.15372165582348732  |   0.20567421671378322  |   0.042232591867946916  |                     1  |    0.1369998199098081  |    ?  |
      Thal  |   0.07715137522340708  |     0.2164245479753749  |  -0.22014967056254497  |   0.05578224491505772  |     0.08929791710179728  |   -0.08388064854854185  |   0.03815270577052584  |    -0.1666763672778155  |    0.1865200045881661  |   0.29388020647671004  |    0.13133340300885163  |   0.13699981990980836  |                     1  |    ?  |
       AHD  |                     ?  |                      ?  |                     ?  |                     ?  |                       ?  |                      ?  |                     ?  |                      ?  |                     ?  |                     ?  |                      ?  |                     ?  |                     ?  |    ?  |

```





