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

/** Defines the basic functions that all markers must have */
class MarkerTemplate {

  /** The google.maps.Marker object visible on the map */
  googleMarker_;

  /** The color of the marker */
  markerColor_;

  constructor() {
    this.googleMarker_ = new google.maps.Marker();
    this.markerColor_ = ColorPicker.DEFAULT_COLOR;
    this.setColor(this.markerColor_);
    this.setMarkerListeners_();
  }

  /**
   * Opens the information window related to this marker
   * @abstract
   */
  openInfoWindow() {}

  /**
   * Closes the information window related to this marker
   * @abstract
   */
  closeInfoWindow() {}

  /**
   * Deletes the marker from the map
   * @abstract
   */
  remove() {}

  /**
   * Sets the color of the marker icon
   * @param {String} color the color of the marker
   */
  setColor(color) {
    this.markerColor_ = color;
    this.changeIcon_(color);
  }

  /** Returns the marker's current color */
  getColorName() {
    return this.markerColor_;
  }

  /**
   * @Private
   * Changes the icon of this marker
   * @abstract
   */
  changeIcon_(color) {}

  /**
   * @Private
   * Sets marker-triggered events
   */
  setMarkerListeners_() {
    this.googleMarker_.addListener('click', () => {
      closeMarkerMenu();
      this.openInfoWindow();
    });
  }

  /** Returns the google.maps.Marker from this marker */
  getGoogleMarker() {
    return this.googleMarker_;
  }

  /** Removes the marker from the map */
  hide() {
    this.closeInfoWindow();
    this.googleMarker_.setMap(null);
  }

  /** Returns the coordinates of this marker */
  getPosition() {
    return this.googleMarker_.getPosition();
  }

  /**
   * Displays the temp marker at the given coordinates
   * @param {google.maps.LatLng} coords coordinates where to make the marker
   */
  setPosition(coords) {
    let marker = this.googleMarker_;
    marker.setPosition(coords);
    marker.setMap(myMap.getGoogleMap());
  }

  /**
   * Changes the marker's map
   * @param {google.maps.Map} map the map to move the marker to
   */
  setMap(map) {
    this.googleMarker_.setMap(map);
  }

  /** Returns whether the marker is currently visible on the map */
  isVisible() {
    return this.googleMarker_.getMap() !== null;
  }
}
