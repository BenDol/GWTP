/**
 * Copyright 2010 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.gwtp.dispatch.shared;

import com.philbeaudoin.gwtp.dispatch.shared.BatchAction.OnException;

import java.util.List;

/**
 * Contains the list of {@link Result}s from successful actions in the
 * {@link BatchAction}. The order will match the order of the original
 * {@link Action}s listed in the {@link BatchAction}.
 * 
 * <p>
 * If the {@link BatchAction} was specified to have an {@link OnException} value
 * of {@link OnException#CONTINUE}, failed actions will have a
 * <code>null</code> value.
 * 
 * @author David Peterson
 */
public class BatchResult implements Result {

  private static final long serialVersionUID = -7169695166206704296L;

  private List<Result> results;

  /**
   * For serialization.
   */
  BatchResult() {
  }

  /**
   * Creates a new result with the list of results from the batch action, in
   * order.
   * 
   * @param results The list of results.
   */
  public BatchResult( List<Result> results ) {
    this.results = results;
  }

  public List<Result> getResults() {
    return results;
  }
}