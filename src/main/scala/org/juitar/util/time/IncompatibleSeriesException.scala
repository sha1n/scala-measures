package org.juitar.util.time

class IncompatibleSeriesException(reportedSeries: String, currentSeries: String)
  extends RuntimeException(s"'$reportedSeries' cannot be added to series '$currentSeries'")
