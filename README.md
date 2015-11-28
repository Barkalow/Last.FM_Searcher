# Last.FM_Searcher
Simple Android project that searches Last.FM for artist, album, and chart data.

Various Handlers are used to parse XML data that is received from the Last.fm API. 

MainActivity hosts the general program flow, deciding what is shown in the lower charts if they are selected as well as
initiating API calls based on user input.

Summary is simply a list view that shows requested data as links, leading to Last.fm pages or a YouTube API search depending
on what was requested.
