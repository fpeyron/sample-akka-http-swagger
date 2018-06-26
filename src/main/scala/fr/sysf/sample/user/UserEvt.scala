package fr.sysf.sample.user

sealed trait UserEvt {
  def id: String
}

case class UserCreatedEvt(
                           id: String,
                           firstName: Option[String],
                           lastName: Option[String],
                           email: String,
                           profileImage: Option[String]
                         ) extends UserEvt

case class UserUpdatedEvt(
                           id: String,
                           firstName: Option[String],
                           lastName: Option[String],
                           email: Option[String],
                           profileImage: Option[String]
                         ) extends UserEvt

case class UserDebitedEvt(
                           id: String,
                           amount: Int,
                           label: String
                         ) extends UserEvt

case class UserCreditedEvt(
                            id: String,
                            amount: Int,
                            label: String
                          ) extends UserEvt

// State
case class UserState(
                      id: String,
                      firstName: Option[String] = None,
                      lastName: Option[String] = None,
                      email: String,
                      profileImage: Option[String] = None,
                      balance: Int = 0
                    )