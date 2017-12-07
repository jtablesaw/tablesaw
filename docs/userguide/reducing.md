Reduce functions
================

An important set of table operations tables compute totals and subtotals. In the relational world, these are implemented by combining summary operations (sum, max…) and group by. Data scientists often refer to these as Split-Apply-Combine functions. Tablesaw tables make basic group operations easy.

In this example, we’ll use a Tornado dataset from the NOAA’s Storm Prediction Center with just under 60,000 rows. It contains records of every US tornado from 1950-2014.  Once we’ve loaded the data, computing stats on subgroups is easy.

    Table avgInjuries = table.average("Injuries").by("Scale");
    
That’s all there is to it. To see the result, we can use the print() method:

    avgInjuries.print();

which produces:

    data/1950-2014_torn.csv summary
    Scale Average Injuries     
    -9.0  0.10526315789473685  
    0.0   0.028898762530750115 
    1.0   0.34711490215755186  
    2.0   1.7164844969886113   
    3.0   9.266158169409863    
    4.0   54.99675850891414    
    5.0   186.31884057971016   

In this dataset, missing data are indicated by a scale of -9. A scale of 0 to 5 
indicates the size of the storm, with 5 being the largest/most severe. 
As you can see, injuries increase dramatically with the more severe storms.

To sum the number of fatalities by state and scale, we would write:

    sumFatalities = table.sum("Fatalities").by("State", "Scale");

which produces:

    data/1950-2014_torn.csv summary
    State Scale Sum Fatalities 
    AL    0.0   1.0            
    AL    1.0   15.0           
    AL    2.0   28.0           
    AL    3.0   86.0           
    AL    4.0   272.0          
    AL    5.0   224.0          
    AR    -9.0  1.0            
    AR    0.0   0.0            
    AR    1.0   5.0          
  
etc.

Since the result returned is also a Table, you can easily perform other operations. For example, to see only storms in Texas and Oklahoma, you could do the following.

    List states = Lists.newArrayList("TX", "OK");
    sumFatalities.selectIf(column("State").isContainedIn(states));

producing:

    data/1950-2014_torn.csv summary
    State Scale Sum Fatalities 
    OK    0.0   0.0            
    OK    1.0   5.0            
    OK    2.0   22.0           
    OK    3.0   71.0           
    OK    4.0   143.0          
    OK    5.0   96.0           
    TX    -9.0  0.0            
    TX    0.0   2.0            
    TX    1.0   21.0           
    TX    2.0   40.0           
    TX    3.0   88.0           
    TX    4.0   219.0          
    TX    5.0   174.0  
        
Data: The tornado dataset is from [NOAA's Storm Prediction Center Severe Weather GIS](http://www.spc.noaa.gov/gis/svrgis/).

Code: The code can be found in the BasicGroupby.java example in the Tablesaw repo on Github.

