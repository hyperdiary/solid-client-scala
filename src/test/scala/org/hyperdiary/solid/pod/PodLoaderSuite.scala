package org.hyperdiary.solid.pod

class PodLoaderSuite extends munit.FunSuite {

  test("test") {
    val loader = new PodLoader("http://krw.localhost:3000/")
    //loader.createContainers(List("journal","entry","label","person","place","residence","thing","photo"))
    for {
      resource <- loader.generateResource("label")
    } yield println(resource)
  }

  test("test2") {
    val loader = new PodLoader("http://krw.localhost:3000/")
    val labelFilePath = getClass.getResource("/labels.csv").getPath
    val labels = loader.getLabels(labelFilePath: String)
    val rdfLabels = labels.map { label =>
      val res = loader.generateResource("label")
      res.flatMap(uri => Some(label.asRdf(uri)))
    }
  }

}
