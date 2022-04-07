# New York City Uber Pickups: K-Means Clustering with Smile + Tablesaw

In previous tutorials, we have covered the popular supervised learning techniques, linear regression and Random Forest classification, that take a defined set of features to predict a response. 

With K-Means, we introduce the notion of an unsupervised learning task, in this case with the desire to cluster observations into similiar groups without the defined input-output structure of regression or classification. 

Our reference text for this tutorial is Chapter 12 (Unsupervised Learning) of the widely used and freely available textbook, <a href="https://www.statlearning.com/">An Introduction to Statistical Learning, Second Edition</a>. The dataset comes from the  <a href="https://data.fivethirtyeight.com/">FiveThirtyEight Repository</a> and can be downloaded here. Other clustering tutorials on this dataset can be found here and here.  (TODO ADD LINKS)


In K-Means, every observation is assigned to 1, and only 1, cluster, and a good cluster is one where within-cluster variation is as low as possible. 

### K-Means Algorithm Intuition
```
 1. Randomly assign each observation to a cluster. 
 2. Until the clusters stop changing: 
 
      a. Compute each cluster's centroid.
      
      b. Reassign each observation to the cluster with the closest centroid. 
```
 
 Essentially, this becomes a computationally-intensive optimization problem to which K-means provides a local optimal solution. Because K-means provides a local optimal, its results change based on the random assignment in step 1. (In practice, Smile uses a variation of the algorithm known as kmean++ that strategically selects initial clusters) (add link?). 
 
 
