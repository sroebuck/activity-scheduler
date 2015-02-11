"use strict";

/* global React */
/* global _ */
/* global $:false */
/* global overallScheduleDisplayNode */

var textAlignCenter = { textAlign: "center" };

var ACTIVITIES = ["Archery", "Trail Biking", "Ropes Course", "High Ropes", "Adventure Golf", "Baking", "Crafts", "Grylls Skylls", "Video workshop", "Mental Mayhem", "Indoor Games", "Games Hall", "Football", "Adventure Playground"];

var getHashQuery = function () {
  return location.hash.replace(/^#/, "");
};

var OverallScheduleDisplay = React.createClass({
  displayName: "OverallScheduleDisplay",
  getInitialState: function () {
    return {
      showing: getHashQuery() || "preferences",
      isLoading: false,
      plans: []
    };
  },
  componentDidMount: function () {
    var _this = this;
    $.ajax({
      dataType: "json",
      url: this.props.source,
      timeout: 120000,
      success: function (result) {
        _this.setState({
          plans: result.plans
        });
      }
    });
    window.onpopstate = function (event) {
      return _this.setState({
        showing: getHashQuery()
      });
    };
  },
  render: function () {
    var tabToShow = undefined;
    var showing = this.state.showing;
    if (showing == "activity") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Activity Programme"
        ),
        React.createElement(SlotsTableEntity, { plans: this.state.plans })
      );
    } else if (showing == "schedules") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Individual Schedules"
        ),
        React.createElement(ScheduleTableElement, { plans: this.state.plans })
      );
    } else if (showing == "schedules-grouped") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Individual Schedules"
        ),
        React.createElement(ScheduleTableGroupedElement, { plans: this.state.plans })
      );
    } else if (showing == "preferences") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Individual Preferences"
        ),
        React.createElement(IndividualPreferencesEntity, { plans: this.state.plans })
      );
    } else if (showing == "prefs-grouped") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Individual Preferences Grouped"
        ),
        React.createElement(IndividualPreferencesSortedByGroupEntity, { plans: this.state.plans })
      );
    } else if (showing == "upload") {
      tabToShow = React.createElement(
        "div",
        null,
        React.createElement(
          "h2",
          null,
          "Upload Preferences"
        ),
        React.createElement(FileForm, { url: "/data/preferences.csv", parent: this })
      );
    }

    var progressBar = undefined;
    if (this.state.isLoading) {
      progressBar = React.createElement(
        "div",
        null,
        React.createElement("br", null),
        React.createElement(
          "div",
          { className: "well well-sm" },
          React.createElement(
            "p",
            null,
            "Uploading new data"
          ),
          React.createElement(
            "div",
            { className: "progress" },
            React.createElement(
              "div",
              { className: "progress-bar progress-bar-striped active", role: "progressbar", ariaValuenow: "45", ariaValuemin: "0", ariaValuemax: "100", style: { width: "45%" } },
              React.createElement(
                "span",
                { className: "sr-only" },
                "45% Complete"
              )
            )
          )
        )
      );
    } else {
      progressBar = React.createElement("div", null);
    }

    return React.createElement(
      "div",
      null,
      React.createElement(
        "ul",
        { className: "nav nav-tabs" },
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "preferences" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#preferences" },
            "Preferences"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "prefs-grouped" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#prefs-grouped" },
            "Preferences Grouped"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "schedules" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#schedules" },
            "Schedules"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "schedules-grouped" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#schedules-grouped" },
            "Schedules Grouped"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "activity" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#activity" },
            "Activity Programme"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "upload" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#upload" },
            "Upload Preferences"
          )
        )
      ),
      progressBar,
      tabToShow
    );
  }
});

var ScheduleTableElement = React.createClass({
  displayName: "ScheduleTableElement",
  render: function () {
    var sortedPlans = _.sortBy(this.props.plans, function (plan) {
      return plan.individual.name;
    });
    return React.createElement(
      "div",
      null,
      React.createElement(
        "table",
        { className: "table table-striped table-condensed" },
        React.createElement(
          "thead",
          null,
          React.createElement(
            "tr",
            null,
            React.createElement(
              "th",
              null,
              "Name"
            ),
            React.createElement(
              "th",
              { style: textAlignCenter },
              "Group"
            ),
            React.createElement(
              "th",
              null,
              "11am-12noon"
            ),
            React.createElement(
              "th",
              null,
              "12noon-1pm"
            ),
            React.createElement(
              "th",
              null,
              "2pm-3pm"
            ),
            React.createElement(
              "th",
              null,
              "3pm-4pm"
            )
          )
        ),
        React.createElement(
          "tbody",
          null,
          sortedPlans.map(function (plan) {
            var i = plan.individual;
            return React.createElement(IndividualTableLine, { key: i.name, name: i.name, group: i.group, ratings: i.ratings, places: plan.places });
          })
        )
      )
    );
  }
});

var ScheduleTableGroupedElement = React.createClass({
  displayName: "ScheduleTableGroupedElement",
  render: function () {
    var sortedPlans = _.sortBy(this.props.plans, function (plan) {
      return [plan.individual.group, plan.individual.name];
    });
    return React.createElement(
      "div",
      null,
      React.createElement(
        "table",
        { className: "table table-striped table-condensed" },
        React.createElement(
          "thead",
          null,
          React.createElement(
            "tr",
            null,
            React.createElement(
              "th",
              null,
              "Name"
            ),
            React.createElement(
              "th",
              { style: textAlignCenter },
              "Group"
            ),
            React.createElement(
              "th",
              null,
              "11am-12noon"
            ),
            React.createElement(
              "th",
              null,
              "12noon-1pm"
            ),
            React.createElement(
              "th",
              null,
              "2pm-3pm"
            ),
            React.createElement(
              "th",
              null,
              "3pm-4pm"
            )
          )
        ),
        React.createElement(
          "tbody",
          null,
          sortedPlans.map(function (plan) {
            var i = plan.individual;
            return React.createElement(IndividualTableLine, { key: i.name, name: i.name, group: i.group, ratings: i.ratings, places: plan.places });
          })
        )
      )
    );
  }
});

var ratingsToPreferences = function (ratingsObj) {
  return new Map(_.pairs(ratingsObj));
};

var IndividualTableLine = React.createClass({
  displayName: "IndividualTableLine",
  render: function () {
    var _props = this.props;
    var preferences = ratingsToPreferences(_props.ratings);
    var places = new Map(_props.places.map(function (place) {
      return [place.slot, place.activity];
    }));
    var a1 = places.get("11am-12noon");
    var a2 = places.get("12noon-1pm");
    var a3 = places.get("2pm-3pm");
    var a4 = places.get("3pm-4pm");
    return React.createElement(
      "tr",
      null,
      React.createElement(
        "th",
        null,
        _props.name
      ),
      React.createElement(
        "td",
        { style: textAlignCenter },
        _props.group
      ),
      React.createElement(ActivityEntity, { activity: a1, preferences: preferences, display: a1 }),
      React.createElement(ActivityEntity, { activity: a2, preferences: preferences, display: a2 }),
      React.createElement(ActivityEntity, { activity: a3, preferences: preferences, display: a3 }),
      React.createElement(ActivityEntity, { activity: a4, preferences: preferences, display: a4 })
    );
  }
});

var ActivityEntity = React.createClass({
  displayName: "ActivityEntity",
  render: function () {
    var _props = this.props;
    var display = _props.display;
    var activity = _props.activity;
    var preference = _props.preferences.get(activity);
    var theStyle = {};
    if (preference <= 3) {
      theStyle = { color: "green" };
    } else if (preference >= 5) {
      theStyle = { color: "red" };
    }
    return (
      // <td style={theStyle}>{display} <span className="badge">{preference}</span></td>
      React.createElement(
        "td",
        { style: theStyle },
        display
      )
    );
  }
});

var IndividualEntity = React.createClass({
  displayName: "IndividualEntity",
  render: function () {
    var _props = this.props;
    return _props.display.replace(/ /g, " ").join(", ");
  }
});

var SlotsTableEntity = React.createClass({
  displayName: "SlotsTableEntity",
  render: function () {
    var plans = this.props.plans.map(function (plan) {
      return plan.places.map(function (slot) {
        return {
          name: plan.individual.name,
          ratings: plan.individual.ratings,
          activity: slot.activity,
          slot: slot.slot };
      });
    });
    var flattened = _.flatten(plans);
    var grouped = _.groupBy(flattened, function (entry) {
      return entry.slot;
    });
    return React.createElement(
      "div",
      null,
      React.createElement(SlotTableEntity, { slot: "11am - 12noon", activities: grouped["11am-12noon"] }),
      React.createElement(SlotTableEntity, { slot: "12noon - 1pm", activities: grouped["12noon-1pm"] }),
      React.createElement(SlotTableEntity, { slot: "2pm - 3pm", activities: grouped["2pm-3pm"] }),
      React.createElement(SlotTableEntity, { slot: "3pm - 4pm", activities: grouped["3pm-4pm"] })
    );
  }
});

var SlotTableEntity = React.createClass({
  displayName: "SlotTableEntity",
  render: function () {
    var activities = this.props.activities;
    var grouped = _.chain(activities).groupBy(function (entry) {
      return entry.activity;
    }).pairs().sortBy(function (activity, entries) {
      return activity;
    }).value();
    return React.createElement(
      "div",
      null,
      React.createElement(
        "h3",
        null,
        this.props.slot
      ),
      React.createElement(
        "table",
        { className: "table table-striped table-condensed" },
        React.createElement(
          "tbody",
          null,
          grouped.map(function (group) {
            var activity = group[0];
            return React.createElement(
              "tr",
              { key: activity },
              React.createElement(
                "th",
                null,
                activity.replace(/ /g, " ")
              ),
              React.createElement(
                "td",
                null,
                group[1].map(function (x) {
                  return x.name.replace(/ /g, " ");
                }).sort().join(", ")
              )
            );
          })
        )
      )
    );
  }
});

var IndividualPreferencesEntity = React.createClass({
  displayName: "IndividualPreferencesEntity",
  render: function () {
    var sortedPlans = _.sortBy(this.props.plans, function (plan) {
      return plan.individual.name;
    });
    return React.createElement(
      "div",
      null,
      React.createElement(
        "table",
        { className: "table table-striped table-condensed table-bordered" },
        React.createElement(
          "thead",
          null,
          React.createElement(
            "tr",
            null,
            React.createElement(
              "th",
              null,
              "Name"
            ),
            React.createElement(
              "th",
              { style: textAlignCenter },
              "Group"
            ),
            ACTIVITIES.map(function (a) {
              return React.createElement(
                "th",
                { style: { textAlign: "center" }, key: a },
                a
              );
            })
          )
        ),
        React.createElement(
          "tbody",
          null,
          sortedPlans.map(function (plan) {
            var i = plan.individual;
            return React.createElement(IndividualPreferencesLine, { key: i.name, name: i.name, group: i.group, ratings: i.ratings, places: plan.places });
          })
        )
      )
    );
  }
});

var IndividualPreferencesSortedByGroupEntity = React.createClass({
  displayName: "IndividualPreferencesSortedByGroupEntity",
  render: function () {
    var sortedPlans = _.sortBy(this.props.plans, function (plan) {
      return [plan.individual.group, plan.individual.name];
    });
    return React.createElement(
      "div",
      null,
      React.createElement(
        "table",
        { className: "table table-striped table-condensed table-bordered" },
        React.createElement(
          "thead",
          null,
          React.createElement(
            "tr",
            null,
            React.createElement(
              "th",
              null,
              "Name"
            ),
            React.createElement(
              "th",
              { style: textAlignCenter },
              "Group"
            ),
            ACTIVITIES.map(function (a) {
              return React.createElement(
                "th",
                { style: { textAlign: "center" }, key: a },
                a
              );
            })
          )
        ),
        React.createElement(
          "tbody",
          null,
          sortedPlans.map(function (plan) {
            var i = plan.individual;
            return React.createElement(IndividualPreferencesLine, { key: i.name, name: i.name, group: i.group, ratings: i.ratings, places: plan.places });
          })
        )
      )
    );
  }
});

var IndividualPreferencesLine = React.createClass({
  displayName: "IndividualPreferencesLine",
  render: function () {
    var _props = this.props;
    var places = _props.places.map(function (p) {
      return p.activity;
    });
    var preferences = ratingsToPreferences(_props.ratings);
    var ratings = new Map(_.pairs(_props.ratings));
    return React.createElement(
      "tr",
      null,
      React.createElement(
        "th",
        null,
        _props.name.replace(/ /g, " ")
      ),
      React.createElement(
        "td",
        { style: textAlignCenter },
        _props.group
      ),
      ACTIVITIES.map(function (a) {
        return React.createElement(PreferenceEntity, { key: a, activity: a, preferences: preferences, isPlaced: _.includes(places, a) });
      })
    );
  }
});

var PreferenceEntity = React.createClass({
  displayName: "PreferenceEntity",
  render: function () {
    var _props = this.props;
    var activity = _props.activity;
    var isPlaced = _props.isPlaced;
    var preference = _props.preferences.get(activity);
    var className = "";
    if (isPlaced) {
      if (preference <= 3) {
        className = "success";
      } else if (preference >= 5) {
        className = "danger";
      } else {
        className = "info";
      }
    }
    return React.createElement(
      "td",
      { style: { textAlign: "center" }, className: className },
      preference
    );
  }
});


// this creates a React component that can be used in other components or
// used directly on the page with React.renderComponent
var FileForm = React.createClass({
  displayName: "FileForm",


  // since we are starting off without any data, there is no initial value
  getInitialState: function () {
    return {
      data_uri: null };
  },

  // prevent form from submitting; we are going to capture the file contents
  handleSubmit: function () {
    var parent = this.props.parent;
    $.ajax({
      url: this.props.url,
      type: "POST",
      timeout: 120000,
      data: this.state.data_uri,
      dataType: "json",
      success: (function (data) {
        parent.setState({
          isLoading: false,
          plans: data.plans
        });
      }).bind(this),
      error: (function (xhr, status, err) {
        parent.setState({ isLoading: false });
      }).bind(this)
    });
    parent.setState({ isLoading: true });
    return false;
  },

  // when a file is passed to the input field, retrieve the contents as a
  // base64-encoded data URI and save it to the component's state
  handleFile: function (e) {
    var self = this;
    var reader = new FileReader();
    var file = e.target.files[0];

    reader.onload = function (upload) {
      self.setState({
        data_uri: upload.target.result });
      self.handleSubmit();
    };
    reader.readAsDataURL(file);
  },

  // return the structure to display and bind the onChange, onSubmit handlers
  render: function () {
    // since JSX is case sensitive, be sure to use 'encType'
    return React.createElement(
      "form",
      { onSubmit: this.handleSubmit, encType: "multipart/form-data" },
      React.createElement("input", { type: "file", onChange: this.handleFile })
    );
  } });

React.render(React.createElement(OverallScheduleDisplay, { source: "/data/test.json" }), overallScheduleDisplayNode);