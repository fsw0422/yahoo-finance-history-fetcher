# Getting

    // As of Akka Streams' Scala version compatibility, Scala 2.11, 2.12 is supported
    "com.github.fsw0422" %% "yahoofinancehistoryfetcher" % "0.1.1"

# Usage

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.clicks
    val interval = "1d"
    val response = Await.result(fetcher.getStockHistory(ticker, startDate, endDate, interval), 100 seconds)
    println(response)
