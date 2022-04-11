# New York City Uber Pickups: K-Means Clustering with Smile + Tablesaw

In previous tutorials, we have covered the popular supervised learning techniques, linear regression and Random Forest classification, that take a defined set of features to predict a response. 

With K-Means, we introduce the notion of an unsupervised learning task, in this case with the desire to cluster observations into similiar groups without the defined input-output structure of regression or classification. 

Our reference text for this tutorial is Chapter 12 (Unsupervised Learning) of the widely used and freely available textbook, <a href="https://www.statlearning.com/">An Introduction to Statistical Learning, Second Edition</a>. The dataset comes from the <a href="https://data.fivethirtyeight.com/">FiveThirtyEight Repository</a> and additional tutorials covering this dataset can be found <a href="https://towardsdatascience.com/how-does-uber-use-clustering-43b21e3e6b7d">here</a> and <a href="https://www.linkedin.com/pulse/uber-trip-segmentation-using-k-means-clustering-khatre-csm-pmp/">here</a> . 


In K-Means, every observation is assigned to 1, and only 1, cluster, and a good cluster is one where within-cluster variation is as low as possible. 

### K-Means Algorithm Intuition
```
 1. Randomly assign each observation to a cluster. 
 2. Until the clusters stop changing: 
 
      a. Compute each cluster's centroid.
      
      b. Reassign each observation to the cluster with the closest centroid. 
```
 
 Essentially, this becomes a computationally-intensive optimization problem to which K-means provides a local optimal solution. Because K-means provides a local optimal, its results change based on the random assignment in step 1. (In practice, Smile uses a variation of the algorithm known as <a href="https://en.wikipedia.org/wiki/K-means%2B%2B?msclkid=4118fed8b9c211ecb86802b7ac83b079#Improved_initialization_algorithm">K-means++</a> that strategically selects initial clusters)
 
 ### K-Means with NYC Uber Data
 
 Because the K-means algorithm clusters datapoints, we can use it to determine ideal locations for Uber drivers to wait in between customer pickups. The data in uber-pickups-april14.csv represents all Uber pickups in New York City during April 2014. To start, create a bounding box around the data to focus on Manhatten and the immediate vicinity. 
 
 ```Java
 Table pickups = Table.read().csv("uber-pickups-apr14.csv");

pickups = pickups.dropWhere(pickups.doubleColumn("lat").isGreaterThan(40.91));
pickups = pickups.dropWhere(pickups.doubleColumn("lat").isLessThan(40.50));

pickups = pickups.dropWhere(pickups.doubleColumn("lon").isGreaterThan(-73.8));
pickups = pickups.dropWhere(pickups.doubleColumn("lon").isLessThan(-74.05));
 ```
 
 Now, randomly select a subset of the data to work with. (This is purely to speed up graphing of the clusters later). 
 
 ```Java
 pickups = pickups.sampleN(100000);
 ```
 
 Format the existing Date/Time Text Column as two new columns, a LocalDateTimeColumn and a LocalTime Column. 
 
 ```Java
 List<String> dateTimes = pickups.textColumn("Date/Time").asList();

        DateTimeColumn dateTimesAsLocalDateTime = DateTimeColumn.create("PickupDateTime");
        TimeColumn timeAsTimeColumn = TimeColumn.create("PickupTime");

        for(String dt: dateTimes)
        {
            dateTimesAsLocalDateTime.append(LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("M/d/yyyy H:m")));
            timeAsTimeColumn.append(LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("M/d/yyyy H:m")).toLocalTime());
        }
        pickups = pickups.replaceColumn("Date/Time", dateTimesAsLocalDateTime);
        pickups.addColumns(timeAsTimeColumn);
 ```
 
 Print out a portion of your data to verify that it is in the correct format. 
 ```Java
 System.out.println(pickups);
 
 >                          uber-pickups-apr14.csv                            
     PickupDateTime       |    Lat    |    Lon     |   Base   |  PickupTime  |
------------------------------------------------------------------------------
 2014-04-01T00:28:00.000  |  40.7588  |  -73.9776  |  B02512  |    00:28:00  |
 2014-04-01T00:33:00.000  |  40.7594  |  -73.9722  |  B02512  |    00:33:00  |
 2014-04-01T01:19:00.000  |  40.7256  |  -73.9869  |  B02512  |    01:19:00  |
 2014-04-01T01:49:00.000  |  40.7271  |  -73.9803  |  B02512  |    01:49:00  |
 ```
 
 Train an initial K-means model with three clusters. 
 
 ```Java
KMeans model = KMeans.fit(pickups.as().doubleMatrix(),3);
Table plot_data = pickups.copy();
plot_data.addColumns(IntColumn.create("cluster",model.y));
Plot.show(ScatterPlot.create("K=3", plot_data, "lon", "lat", "cluster"));
 ```
 
 Your plot should look similiar to the following. You can clearly see the three chosen clusters represented by the color of each pickup location. (Notice that the data mirrors the shape of Manhatten and the surrounding area)
 
 TODO Insert image(s) here
 
 
 A practical issue encountered when using the K-means algorithm is the choice of the number of clusters, K. A common approach is to create an "Elbow Curve", which is a plot of the distortion (sum of squared distances from the centroid of a cluster) against chosen values of k. Let's create an Elbow Curve for each value of k from 2,10). 
 
 ```Java
 Table plot_data = pickups.copy();
plot_data.addColumns(IntColumn.create("cluster",model.y));
Plot.show(ScatterPlot.create("K=3", plot_data, "lon", "lat", "cluster"));

Table elbowTable = Table.create("Elbow", DoubleColumn.create("Distortion", 9));
elbowTable.addColumns(IntColumn.create("k", 10));
for(int k = 2; k < 10; k++)
  {
KMeans model2 = KMeans.fit(pickups.as().doubleMatrix(),k);
elbowTable.doubleColumn("Distortion").set(k, model2.distortion);
elbowTable.intColumn("k").set(k, k);
   }
 ```
 
 Your curve should look something like the image below. We are looking for a hard break in the curve at a value of k where the distortion flattens out. (Hence the name, *Elbow Curve*)
 
 INSERT IMAGE
 
 Based on this curve, I will choose k=5. Generate a new model with k=5. This time, generate an additional plot showing the centroids of each region. 
 
 ```Java
 KMeans modelBest = KMeans.fit(pickups.as().doubleMatrix(),5);
  Table plot_data_best = pickups.copy();
plot_data_best.addColumns(IntColumn.create("cluster",modelBest.y));
Plot.show(ScatterPlot.create("K=5", plot_data_best, "lon", "lat", "cluster"));

Table centTable = Table.create("Centroids",DoubleColumn.create("lat", modelBest.centroids.length), DoubleColumn.create("lon", modelBest.centroids.length));

for(int i = 0; i < modelBest.centroids.length; i++)
{
centTable.doubleColumn("lat").set(i, modelBest.centroids[i][0]);
centTable.doubleColumn("lon").set(i, modelBest.centroids[i][1]);
}

Plot.show(ScatterPlot.create("centroids", centTable, "lon", "lat"));
 ```
 
 We now have a reasonable assessment of where idled Uber drivers should congregated as they wait for their next pickup: the centroids of our 5 regions. 
 
 TODO Insert Image(s)
 
 
So far in our analysis, we have sought to develop a general recommendation for where a driver should idle irrespective of the day of the week or time of the day. Now, let's look at how these factors influence the ideal location. 

**Late Night (11 pm-5 am)**

```Java
\\Late Night (11 pm-5 am)
Table lateNight = pickups.where(pickups.timeColumn("PickupTime").isAfter(LocalTime.of(23,0)).or(pickups.timeColumn("PickupTime").isBefore(LocalTime.of(5,0))));
KMeans modelLateNight = KMeans.fit(lateNight.as().doubleMatrix(),5);
Table plot_data_lateNight = lateNight.copy();
plot_data_lateNight.addColumns(IntColumn.create("cluster",modelLateNight.y));
Plot.show(ScatterPlot.create("Late Night, K=5", plot_data_lateNight, "lon", "lat", "cluster"));
```

**Weekday Mornings and Evenings**
```Java
Table weekdays = pickups.where(pickups.dateTimeColumn("PickupDateTime")
                .isMonday()
                .or(pickups.dateTimeColumn("PickupDateTime").isTuesday())
                .or(pickups.dateTimeColumn("PickupDateTime").isWednesday())
                .or(pickups.dateTimeColumn("PickupDateTime").isThursday()));
                
\\Weekday Morning (M-Th, 6 am-10 am)
Table weekdayMorning = weekdays.where(weekdays.timeColumn("PickupTime").isAfter(LocalTime.of(6, 0))
                .and(weekdays.timeColumn("PickupTime").isBefore(LocalTime.of(10,0)));
KMeans modelWeekdayMorning = KMeans.fit(weekdayMorning.as().doubleMatrix(),5);
Table plot_data_WeekdayMorning = weekdayMorning.copy();
plot_data_WeekdayMorning.addColumns(IntColumn.create("cluster",modelWeekdayMorning.y));
Plot.show(ScatterPlot.create("Weekday Morning, K=5", plot_data_WeekdayMorning, "lon", "lat", "cluster"));               
\\Weekday Evening (M-Th, 5 pm-10 pm)
Table weekdayEvening =  weekdays.where(weekdays.timeColumn("PickupTime").isAfter(LocalTime.of(17, 0))
                .and(weekdays.timeColumn("PickupTime").isBefore(LocalTime.of(22,0)));
                
KMeans modelWeekdayEvening = KMeans.fit(weekdayEvening.as().doubleMatrix(),5);
Table plot_data_WeekdayEvening = weekdayEvening.copy();
plot_data_WeekdayEvening.addColumns(IntColumn.create("cluster",modelWeekdayEvening.y));
Plot.show(ScatterPlot.create("Weekday Evening, K=5", plot_data_WeekdayEvening, "lon", "lat", "cluster"));    
```

**Weekends**
```Java
\\Weekend
Table weekend =  pickups.where(pickups.dateTimeColumn("PickupDateTime")
                .isSaturday()
                .or(pickups.dateTimeColumn("PickupDateTime").isSunday())
                
KMeans modelWeekend = KMeans.fit(weekend.as().doubleMatrix(),5);
Table plot_data_Weekend = weekend.copy();
plot_data_Weekend.addColumns(IntColumn.create("cluster",modelWeekend.y));
Plot.show(ScatterPlot.create("Weekend, K=5", plot_data_Weekend, "lon", "lat", "cluster"));                
 
 ```
 
//TODO add pictures of all 4 model plots
 
