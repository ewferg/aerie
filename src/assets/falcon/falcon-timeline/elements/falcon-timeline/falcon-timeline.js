/* global FalconPolymerUtils */

/**
 * Falcon Timeline Web Component.
 *
 * @demo demo/index.html
 * @polymer
 * @customElement
 * @appliesMixin Polymer.IronResizableBehavior
 * @appliesMixin FalconPolymerUtils
 */
class FalconTimeline extends Polymer.mixinBehaviors([Polymer.IronResizableBehavior, FalconPolymerUtils], Polymer.Element) {
  /**
   * Get the name of this element.
   *
   * @readonly
   * @static
   *
   * @memberof FalconTimeline
   */
  static get is() {
    return 'falcon-timeline';
  }

  /**
   * Get the properties of this element.
   *
   * @readonly
   * @static
   *
   * @memberof FalconTimeline
   */
  static get properties() {
    return {
      /**
       * The Polymer debounce object for de-bouncing iron resize events.
       */
      _onIronResizeDebouncer: {
        readOnly: true,
        type: Object,
        value: () => {
          return new Polymer.Debouncer();
        },
      },

      /**
       * The list of bands displayed in the timeline.
       * To push a newBand to this list use: this.push('bands', newBand).
       */
      bands: {
        type: Array,
        value: () => [],
      },

      /**
       * The debounce time for iron resize.
       */
      ironResizeDebounceTime: {
        type: Number,
        value: 200,
      },

      /**
       * The label width of all the bands in the timeline.
       */
      labelWidth: {
        type: Number,
        value: 100,
      },

      /**
       * The largest time range calculated from every band in the timeline.
       */
      maxTimeRange: {
        type: Object,
        value: () => {
          return {
            end: 0,
            start: 0,
          };
        },
      },

      /**
       * The current band selected from the list of bands.
       * If a band is selected this object will be one of the elements
       * in the bands list.
       */
      selectedBand: {
        type: Object,
        value: null,
      },

      /**
       * The current time range we are viewing in the timeline.
       */
      viewTimeRange: {
        type: Object,
        value: () => {
          return {
            end: 0,
            start: 0,
          };
        },
      },
    };
  }

  /**
   * Get the observers of this element.
   *
   * @readonly
   * @static
   *
   * @memberof FalconTimeline
   */
  static get observers() {
    return [
      '_bandsChanged(bands)',
      '_selectedBandChanged(selectedBand)',
    ];
  }

  /**
   * Called after the element is attached to the document.
   * Can be called multiple times during the lifetime of an element.
   *
   * @memberof FalconTimeline
   */
  connectedCallback() {
    super.connectedCallback();

    this._addEventListeners();
  }

  /**
   * Called after the element is detached from the document.
   * Can be called multiple times during the lifetime of an element.
   *
   * @memberof FalconTimeline
   */
  disconnectedCallback() {
    super.disconnectedCallback();

    this._removeEventListeners();
  }

  /**
   * Override the default Polymer _attachDom so we
   * attach to the light DOM instead of the shadow DOM.
   *
   * @param {*} dom
   *
   * @memberof FalconTimeline
   */
  _attachDom(dom) {
    Polymer.dom(this).appendChild(dom);
  }

  /**
   * Observer. Called when bands property changes.
   *
   * @param {Array} bands
   *
   * @memberof FalconTimeline
   */
  _bandsChanged() {
    let endTime = Number.MIN_SAFE_INTEGER;
    let startTime = Number.MAX_SAFE_INTEGER;

    // Calculate the maxTimeRange out of every band (including composite bands).
    this.bands.forEach((band) => {
      if (!band.bands && band.maxTimeRange) {
        if (band.maxTimeRange.start < startTime) startTime = band.maxTimeRange.start;
        if (band.maxTimeRange.end > endTime) endTime = band.maxTimeRange.end;
      }
      else if (band.bands) {
        band.bands.forEach((subBand) => {
          if (subBand.maxTimeRange) {
            if (subBand.maxTimeRange.start < startTime) startTime = subBand.maxTimeRange.start;
            if (subBand.maxTimeRange.end > endTime) endTime = subBand.maxTimeRange.end;
          }
        });
      }
    });

    this.set('maxTimeRange', { end: endTime, start: startTime });
    this.set('viewTimeRange', { end: endTime, start: startTime });

    // Dispatch a full window resize event (which triggers an iron resize) to make sure all bands are sized properly.
    window.dispatchEvent(new Event('resize'));
  }

  /**
   * Observer. Called when selectedBand property changes.
   *
   * @memberof FalconTimeline
   */
  _selectedBandChanged() {
    if (this.selectedBand) {
      // Link the new selectedBand to the correct band in the bands list.
      this.bands.forEach((band, index) => {
        if (this.selectedBand.id === band.id) {
          this.linkPaths('selectedBand', `bands.${index}`);
        }
      });

      // Notify listeners of the newly selected band.
      this.fire('falcon-timeline-selected-band-changed', { selectedBand: this.selectedBand });
    }
  }

  /**
   * Add all event listeners.
   *
   * @memberof FalconTimeline
   */
  _addEventListeners() {
    this.addEventListener('iron-resize', this._onIronResize.bind(this));
    document.addEventListener('falcon-on-band-click', this._onBandClick.bind(this)); // Listen on document in case the band has moved out of this element.
  }

  /**
   * Remove all event listeners.
   *
   * @memberof FalconTimeline
   */
  _removeEventListeners() {
    this.removeEventListener('iron-resize', this._onIronResize.bind(this));
    document.removeEventListener('falcon-on-band-click', this._onBandClick.bind(this));
  }

  /**
   * Event listener. Called when a band is clicked.
   *
   * @param {Event} e
   * @memberof FalconTimeline
   */
  _onBandClick(e) {
    const bandElement = e.detail.element;

    if (bandElement && bandElement.id) {
      this.bands.forEach((band, index) => {
        if (bandElement.id === band.id) {
          this.set('selectedBand', this.bands[index]);
          this.linkPaths('selectedBand', `bands.${index}`);
        }
      });
    }
  }

  /**
   * Event listener. Called after component resizes.
   * Debounce the resize since it's redraws.
   * We don't want to redraw too much or CTL might freeze and crash.
   *
   * @memberof FalconTimeline
   */
  _onIronResize() {
    Polymer.Debouncer.debounce(
      this._onIronResizeDebouncer,
      Polymer.Async.timeOut.after(this.ironResizeDebounceTime),
      () => {
        this._resize(this);
        this._resize(document); // Also resize on document root if a band has been moved outside of this element.
      },
    );
  }

  /**
   * Helper that resizes bands based on an element root.
   *
   * @param {any} root
   * @memberof FalconTimeline
   */
  _resize(root) {
    const timeBand = Polymer.dom(root).querySelector('falcon-time-band');
    const timeScrollBar = Polymer.dom(root).querySelector('falcon-time-scroll-bar');
    const allBandElements = Polymer.dom(root).querySelectorAll('.falcon-band');

    if (timeBand) {
      timeBand.resize();
    }

    if (timeScrollBar) {
      timeScrollBar.resize();
    }

    if (allBandElements) {
      allBandElements.forEach((band) => {
        if (band.resize) {
          band.resize();
          band._calculateTickValues();
        }
      });
    }
  }
}

customElements.define(FalconTimeline.is, FalconTimeline);
