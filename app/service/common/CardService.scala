package service.common

import model._

trait CardService {
  def list(userId: Int): Seq[Card]
}
