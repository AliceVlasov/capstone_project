// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Creates moveable temporary marker */
class TempMarker extends MarkerTemplate{

  /** TempInfoWindow linked to this marker */
  tempInfoWindow_;

  constructor() {
    super();

    this.googleMarker_.setLabel("+");
    this.googleMarker_.setDraggable(true);
    this.tempInfoWindow_ = new TempInfoWindow(this);
    this.setTempListeners_();
  }

  /** Opens the temporary information window for this temporary marker */
  openInfoWindow() {
    myMap.closePermInfoWindow()
    this.tempInfoWindow_.open();
  }

  /** Closes the temporary information window for this temporary marker */
  closeInfoWindow() {
    this.tempInfoWindow_.close();
  }

  /** Hide but do not delete the marker */
  remove() {
    super.hide();
  }

  /**
   * @Private
   * Sets click and drag events to the marker
   */
  setTempListeners_() {
    this.googleMarker_.addListener('dragend', () => {
      myMap.panTo(this.getPosition());
    });
  }

  /**
  * Sends the marker given marker details to the map to update a PermMarker
  * @param {String} title new title of the marker
  * @param {String} body new body of the marker
   */
  setPermMarkerInfo(title, body) {
    myMap.sendPermMarkerInfo(this.getPosition(), title, body);
    this.hide();
  }
}
