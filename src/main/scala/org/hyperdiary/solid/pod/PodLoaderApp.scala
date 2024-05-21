package org.hyperdiary.solid.pod

import cats.implicits.*
import com.monovore.decline.{CommandApp, Opts}

object PodLoaderApp extends CommandApp(
      name = "Solid Pod Loader",
      header = "Loads data into a Solid Pod",
      main = {
        val inputFile =
          Opts.option[String]("file", short = "f", help = "File to load")

        val podUrl = Opts.option[String]("pod", short = "p", help = "Pod URL")

        (inputFile,podUrl).mapN { (file,pod) =>
          val loader = new PodLoader(pod)
          loader.loadTurtleFile(file)
        }
      }
    )
