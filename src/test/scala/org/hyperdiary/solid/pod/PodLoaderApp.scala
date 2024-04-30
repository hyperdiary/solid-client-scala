package org.hyperdiary.solid.pod

import cats.implicits.*
import com.monovore.decline.{CommandApp, Opts}

import scala.util.{Failure, Success}

object PodLoaderApp extends CommandApp(
  name = "Solid Pod Loader",
  header = "Loads data into a Solid Pod",
  main = {
    // TODO update parameters
    val inputPath =
      Opts.option[String]("input", short = "i", help = "Input JSON file path")

    val outputPath = Opts.option[String]("output", short = "o", help = "Output XML file path")

    (inputPath, outputPath).mapN { (inPath, outPath) =>
      val loader = new PodLoader("http://krw.localhost:3000/")
//            transformer.jsonToXml(inPath, outPath) match {
//        case Success(_)     => println("Transformation completed successfully!")
//        case Failure(error) => error.printStackTrace()
//      }
    }
  }
)
