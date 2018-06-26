package fr.sysf.sample.user

case class StopCmd(
                    entityId: String
                  )

sealed trait UserCmd {
  def id: String
}


case class UserCreateCmd(
                          id: String,
                          firstName: Option[String],
                          lastName: Option[String],
                          email: String,
                          profileImage: Option[String]
                        ) extends UserCmd

case class UserUpdateCmd(
                          id: String,
                          firstName: Option[String],
                          lastName: Option[String],
                          email: Option[String],
                          profileImage: Option[String]
                        ) extends UserCmd

case class UserDebitCmd(
                         id: String,
                         amount: Int
                       ) extends UserCmd

