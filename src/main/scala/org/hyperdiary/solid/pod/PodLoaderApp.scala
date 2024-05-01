package org.hyperdiary.solid.pod

import com.monovore.decline.{CommandApp, Opts}

object PodLoaderApp extends CommandApp(
      name = "Solid Pod Loader",
      header = "Loads data into a Solid Pod",
      main = {
        val inputPath =
          Opts.option[String]("file", short = "f", help = "File to load")

        inputPath.map { path =>
          val loader = new PodLoader("http://krw.localhost:3000/")
          loader.loadTurtleFile(path)
        }
      }
    )
