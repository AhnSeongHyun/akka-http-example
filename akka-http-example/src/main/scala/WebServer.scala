import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn

import scala.concurrent.Future

/// JSON SERVER POST/GET 123
object WebServer {

    implicit val system = ActorSystem("system")
    implicit val materilaizer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    var orders: List[Item] = Nil

    final case class Item(name: String, id: Long)
    final case class Order(items: List[Item])

    implicit val itemFormat = jsonFormat2(Item)
    implicit val orderFormat = jsonFormat1(Order)

    def fetchItem(itemId: Long): Future[Option[Item]] = Future {
        orders.find(o => o.id == itemId)
    }

    def saveOrder(order: Order):Future[Done] = {
        orders = order match {
            case Order(items) => items:::orders
            case _ => orders
        }
        Future { Done }
    }

    def main(args: Array[String]): Unit ={

        val route = {
            get {
                pathPrefix("item"/LongNumber) { id =>
                    val maybeItem:Future[Option[Item]] = fetchItem(id)
                    onSuccess(maybeItem){
                        case Some(item) => complete(item)
                        case None => complete(StatusCodes.NotFound)
                    }
                }
            } ~
            post {
                path("create-order") {
                    entity(as[Order]) { order =>
                        val saved: Future[Done] = saveOrder(order)
                        onComplete(saved) { done => complete("order created")}
                        
                    }
                }
            }
        }

        val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
        println(s"Server online at http://localhost:8080/\nPress Return Stop...")
        StdIn.readLine()
        bindingFuture
            .flatMap(_.unbind())
            .onComplete(_ => system.terminate())


    }
}
