# Moneyball: Linear regression

Linear regression analysis has been called the "Hello World" of machine learning, because it's easy widely used and easy to understand. It's also very powerful.

One of the best known applications of regression comes from the book <a href="https://www.amazon.com/dp/B000RH0C8G/ref=dp-kindle-redirect?_encoding=UTF8&amp;btkr=1">Moneyball</a>, which describes the use of data science at the Oakland A's baseball team. My analysis is based on a lecture given in the EdX course: <a href="https://www.edx.org/course/analytics-edge-mitx-15-071x-2">MITx: 15.071x The Analytics Edge</a>.  If you're new to data analytics, I would *strongly* recommend this course.

Moneyball is a great example of how to apply data science to solve a business problem. For the A's, the business problem was "How do we make the playoffs?" They break that problem down into simpler problems that can be solved with data. Their approach is summarized in the diagram below:



In baseball, you make the playoffs by winning more games than your rivals. You can't control the number of games your rivals win so the A's looked instead at how many wins it takes to make the playoffs. They decided that 95 wins would give them a strong chance.  Here's how we might check that assumption in Tablesaw.

```Java
// Get the data
Table baseball = Table.read().csv("data/baseball.csv");

// filter to the data available at the start of the 2002 season
Table moneyball = baseball.selectWhere(column("year").isLessThan(2002));
```

We can check the assumption visually by plotting wins per year in a way that separates the teams who make the playoffs from those who don't. This code produces the chart below:

```Java
NumericColumn wins = moneyball.nCol("W");
NumericColumn year = moneyball.nCol("Year");
Column playoffs = moneyball.column("Playoffs");
ScatterPlot.show("Regular season wins by year", moneyball, "W", "year", "playoffs");
```

<img class="alignnone size-full wp-image-1393" src="https://jtablesaw.files.wordpress.com/2016/07/moneyball.png" alt="moneyball" width="1468" height="1080" />

Teams that made the playoffs are shown as greenish points.  If you draw a vertical line at 95 wins, you can see that it's very likely that a team that wins over 95 games will make the playoffs. So far so good.

> **Aside: Cross Tabs** We can also use cross-tabulations (cross-tabs) to quantify the historical data. Cross-tabs calculate the number or percent of observations that fall into various groups. Here we're interested in looking at the interaction between winning more than 95 games and making the playoffs. We start by making a boolean column for more than 95 wins, then create a cross tab between that column and the "playoffs" column. 
>
> ```Java
> // create a boolean column - 'true' means team won more than 95 games
> BooleanColumn ninetyFivePlus =
>         BooleanColumn.create("95+ Wins", wins.isGreaterThanOrEqualTo(95), wins.size());
> moneyball.addColumns(ninetyFivePlus);
> 
> // calculate the column percents
> Table xtab95 = moneyball.xTabColumnPercents("Playoffs", "95+ Wins");
> 
> // format the results to show percents with one decimal place
> xtab95.columnsOfType(ColumnType.NUMBER)
>     .forEach(ea -> 
>              ((NumberColumn)ea).setPrintFormatter(NumberColumnFormatter.percent(1)));
> 
> >        Crosstab Column Proportions:         
>  [labels]  |  false   |   true   |  total   |
> ---------------------------------------------
>       0.0  |   91.9%  |   18.2%  |   82.9%  |
>       1.0  |    8.1%  |   81.8%  |   17.1%  |
>            |  100.0%  |  100.0%  |  100.0%  |
> ```
>
> As you can see from the table roughly 82% of teams who win more than 95 games also made the playoffs. 

Unfortunately, you can't directly control the number of games you win. We need to go deeper. At the next level, we hypothesize that the number of wins can be predicted by the number of Runs Scored during the season, combined with the number of Runs Allowed.

To check this assumption we compute Run Difference as Runs Scored - Runs Allowed:

```java
IntColumn runDifference = 
    moneyball.numberColumn("RS").subtract(moneyball.numberColumn("RA"));
moneyball.addColumn(runDifference);
runDifference.setName("Run Difference");
```

Now lets see if Run Difference is correlated with Wins. We use a scatter plot again:

```Java
Scatter.show("RD x Wins", moneyball, "RD","W");
```

<img class="alignnone size-full wp-image-1409" src="https://jtablesaw.files.wordpress.com/2016/07/rd-vs-wins.png" alt="RD vs Wins" width="600" height="400" />

Our plot shows a strong linear relation between the two. Let's create our first predictive model using linear regression, with runDifference as the explanatory variable.

```Java
LeastSquares winsModel = LeastSquares.train(wins, runDifference);
```

If we print our "winsModel", it produces the output below:

```java
Linear Model:

Residuals:
     Min       1Q    Median       3Q       Max
-14.2662  -2.6511    0.1282   2.9365   11.6570

Coefficients:
             Estimate   Std.Error   t value  Pr(&gt;|t|)
 (Intercept)  80.8814      0.1312  616.6747    0.0000 ***

##  RD            0.1058      0.0013   81.5536    0.0000 ***

Significance codes: 0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1

Residual standard error: 3.9391 on 900 degrees of freedom
Multiple R-squared: 0.8808, Adjusted R-squared: 0.8807
F-statistic: 6650.9926 on 1 and 900 DF, p-value: 0.000
```

If you're new to regression, here are some take-aways from the output:

- The R-squared of 88 can be interpreted to mean that roughly 88% of the variance in Wins can be explained by the Run Difference variable. The rest may be determined by some other variable, or it may be pure chance.
- The estimate for the Intercept is the average wins independent of Run Difference. In baseball, we have a 162 game season so we expect this value to be about 81, as it is.
- The estimate for the RD variable of .1, suggests that an increase of 10 in Run Difference, should produce about 1 additional win over the course of the season.

Of course, this model is not simply descriptive. We can use it to make predictions. In the code below, we predict how many games we will win if we score 135 more runs than our opponents.  To do this, we pass an array of doubles, one for each explanatory variable in our model, to the predict() method. In this case, there's just one variable - run difference.

- ```Java
  double[] runDifference = new double[1];
  runDifference[0] = 135;
  double expectedWins = winsModel.predict(runDifference);
  ```

In this case, expectedWins is 95.2 when we outscore opponents by 135 runs.

It's time to go deeper again and see how we can model Runs Scored and Runs Allowed. The approach the A's took was to model Runs Scored using team On-base percent (OBP) and team Slugging Average (SLG). In Tablesaw, we write:

```java
LeastSquares runsScored2 = 
    LeastSquares.train(moneyball.nCol("RS"), moneyball.nCol("OBP"), moneyball.nCol("SLG"));
```


Once again the first parameter takes a Tablesaw column containing the values we want to predict (Runs scored). The next two parameters take the explanatory variables OBP and SLG.

    Linear Model:
    Residuals:
               Min          1Q      Median          3Q         Max
          -70.8379    -17.1810     -1.0917     16.7812     90.0358
    
    Coefficients:
                Estimate        Std. Error        t value        Pr(&gt;|t|)
    (Intercept)  -804.6271           18.9208       -42.5261          0.0000 ***
    OBP          2737.7682           90.6846        30.1900          0.0000 ***
    SLG          1584.9085           42.1556        37.5966          0.0000 ***
    ---------------------------------------------------------------------
    Significance codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
    
    Residual standard error: 24.7900 on 899 degrees of freedom
    Multiple R-squared: 0.9296,    Adjusted R-squared: 0.9294
    F-statistic: 5933.7256 on 2 and 899 DF,  p-value: 0.000

Again we have a model with excellent explanatory power with an R-squared of 92. Now we'll check the model visually to see if it violates any assumptions. First, our residuals should be normally distributed. We can use a histogram to verify:
Histogram.show(runsScored2.residuals());

<img class="alignnone size-full wp-image-1522" src="https://jtablesaw.files.wordpress.com/2016/07/residuals_histogram.png" alt="residuals_histogram" width="1368" height="968" />

This looks great.  It's also important to plot the predicted (or fitted) values against the residuals. We want to see if the model fits some values better than others, which will influence whether we can trust its predictions or not. We want to see a cloud of random dots around zero on the y axis.

Our Scatter class can create this plot directly from the model:
Scatter.showFittedVsResidual(runsScored2);
<img class="alignnone size-full wp-image-1531" src="https://jtablesaw.files.wordpress.com/2016/07/fittedvresiduals.png" alt="FittedVresiduals" width="600" height="400" />

Again, the plot looks good.

Lets review.  We've created a model of baseball that predicts entry into the playoffs based on batting stats, with the influence of the variables as:

SLG &amp; OBP -&gt; Runs Scored -&gt; Run Difference -&gt; Regular Season Wins

Of course, we haven't modeled the Runs Allowed side of Run Difference. We could use pitching and field stats to do this, but the A's cleverly used the same two variables (SLG and OBP), but now looked at how their opponent's performed against the A's. We could do the same as these data are encoded in the dataset as OOBP and OSLG.

We used regression to build predictive models, and visualizations to check our assumptions and validate our models.

However, we still haven't shown how this knowledge can be applied. That step involves predicting how the current team will perform using historical data, and considering the available talent to see who can bring up the team's average OBP or SLG numbers, or reduce the opponent values of the same stats. They can create scenarios where they consider various trades and expected salary costs. Taking it to that level requires individual player stats that aren't in our dataset, so we'll leave it here, but I hope this post has shown how Tablesaw and Smile makes regression analysis in Java easy and practical.