/*
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
package com.stratio.common.utils.concurrent

import scala.concurrent.{ExecutionContext, Future}

/**
 * Add extra functionality for [[Future]] static functions
 * @param companion Future companion object
 */
class FutureFunctions(companion: Future.type) {

  /**
   * Simple version of Future.traverse. Transforms a Option[Future[A]] into a Future[Option[A]].
   * Useful for reducing optional Futures into a single Future.
   */
  def option[A]
    (in: Option[Future[A]])
    (implicit executor: ExecutionContext): Future[Option[A]] = {
    Future.sequence(in.toList).map(_.headOption)
  }

}
