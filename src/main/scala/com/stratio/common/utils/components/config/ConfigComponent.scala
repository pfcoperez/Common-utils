/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.common.utils.components.config

import scala.util.{Success, Try}

trait ConfigComponent {

  val config: Config

  trait
  Config {

    def getConfig(key: String): Try[Option[Config]]

    def getConfigPath(key: String): Try[Option[Config]] = Success(None)

    def getString(key: String): Try[Option[String]]

    def getString(key: String, default: String): Try[String] = ??? //TODO
      //getString(key) getOrElse default //Use here monad transformers

    def getInt(key: String): Try[Option[Int]]

    def getInt(key: String, default: Int): Try[Int] = ??? //TODO
      //getInt(key) getOrElse default //Monad transformers here please

    def getStringList(key: String): Try[List[String]]

    def getStringList(key: String, default: List[String]): Try[List[String]] = ??? /* TODO: {
      val list = getStringList(key)
      if (list.isEmpty) default else list
    }*/

    def toMap: Try[Map[String, Any]]

    def toStringMap: Try[Map[String, String]]
  }
}
