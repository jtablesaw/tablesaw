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
 
 
 
 
