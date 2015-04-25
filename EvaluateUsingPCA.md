## Background ##

Simbrain can be used to perform a cluster analysis on a set of data.   For a brief overview of what cluster analysis can be used for, see this page:

http://www.clustan.com/what_is_cluster_analysis.html

## Basic Process ##

**Open Simbrain**

[.md](.md)

**Insert  a dataworld**

_(Top Level Menu): Insert  > New World > DataWorld_

**Import your data**

_(Data World Menu): File > Import Data..._

This can be used to import a .csv file (comma-separated-values).  These files can be edited using Excel.

**Configure dataworld**

Set dataworld to iterate mode: _(DataWorldMenu): Edit > Iteration Mode_

**Insert a projection component**

_(Top Level Menu): Insert  > New Plot > Projection Plot_

Leave it set to PCA.  You can experiment with other projections after you've brought in all the data.

**Couple the dataworld component to projection**

_(Data World Menu): Couple  > Projection1_

Todo: explain here about how to set the right number of dimensions in the projection component.

**Do a run through the data by pressing global run**

Todo: explain here how to check that all the data has been run through.

## Analysis ##

The resulting set of points in the projection plot represent a projection of your data

## Links ##

http://en.wikipedia.org/wiki/Principal_components_analysis

http://en.wikipedia.org/wiki/Cluster_analysis_(in_marketing)

http://hisee.sourceforge.net/about.html