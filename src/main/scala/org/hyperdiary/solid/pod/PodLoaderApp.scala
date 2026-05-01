package org.hyperdiary.solid.pod

import cats.implicits.*
import com.monovore.decline.{ CommandApp, Opts }
import org.hyperdiary.solid.client.SolidClient
import org.hyperdiary.solid.dpop.DpopManager

object PodLoaderApp extends CommandApp(
      name = "Solid Pod Loader",
      header = "Loads data into a Solid Pod",
      main = {
        val inputFile =
          Opts.option[String]("file", short = "f", help = "File to load")

        val podUrl = Opts
          .option[String]("pod", short = "p", help = "Pod URL")
          .validate("Pod URL most not end with '/'")(!_.endsWith("/"))

        val baseUrl = Opts
          .option[String]("base", short = "b", help = "Base URL if different from Pod URL")
          .validate("Base URL most not end with '/'")(!_.endsWith("/"))
          .orNone

        val collectionName = Opts
          .option[String]("collection", short = "c", help = "Collection name if loading a Turtle file with fragments")
          .orNone

        val localName =
          Opts.option[String]("local", short = "l", help = "Local name if loading a Turtle file with fragments").orNone

        // TODO - add an option to delete a resource

        (inputFile, podUrl, baseUrl, collectionName, localName).mapN { (file, pod, base, collection, local) =>
          val loader = new PodLoader(new SolidClient(DpopManager()), pod, base)
          if (collection.nonEmpty && local.nonEmpty) {
            loader.loadTurtleHashFile(file, collection.get, local.get)
          } else {
            loader.loadTurtleFile(file)
          }
        }
      }
    )
