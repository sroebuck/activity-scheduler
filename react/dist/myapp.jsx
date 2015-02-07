"use strict";

var _slicedToArray = function (arr, i) { if (Array.isArray(arr)) { return arr; } else { var _arr = []; for (var _iterator = arr[Symbol.iterator](), _step; !(_step = _iterator.next()).done;) { _arr.push(_step.value); if (i && _arr.length === i) break; } return _arr; } };

/* global React */
/* global _ */
/* global $:false */
/* global overallScheduleDisplayNode */

var textAlignCenter = { textAlign: "center" };

var ACTIVITIES = ["Archery", "Trail Biking", "Ropes Course", "High Ropes", "Adventure Golf", "Baking", "Crafts", "Fire Starter", "Video & Photography", "Mental Mayhem", "Indoor Games", "Games Hall", "Football", "Adventure Playground", "Another"];

var OverallScheduleDisplay = React.createClass({
  displayName: "OverallScheduleDisplay",
  getInitialState: function () {
    return {
      showing: "preferences",
      plans: []
    };
  },
  componentDidMount: function () {
    var _this = this;
    $.ajax({
      dataType: "json",
      url: this.props.source,
      timeout: 60000,
      success: function (result) {
        _this.setState({
          plans: result.plans
        });
      }
    });
  },
  handleShowActivitySlots: function (event) {
    this.setState({ showing: "activity" });
  },
  handleShowSchedules: function (event) {
    this.setState({ showing: "schedules" });
  },
  handleShowPreferences: function (event) {
    this.setState({ showing: "preferences" });
  },
  render: function () {
    var tabToShow = undefined;
    if (this.state.showing == "activity") {
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
    } else if (this.state.showing == "schedules") {
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
    } else if (this.state.showing == "preferences") {
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
            { href: "#", onClick: this.handleShowPreferences },
            "Preferences"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "schedules" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#", onClick: this.handleShowSchedules },
            "Schedules"
          )
        ),
        React.createElement(
          "li",
          { role: "presentation", className: this.state.showing == "activity" ? "active" : "" },
          React.createElement(
            "a",
            { href: "#", onClick: this.handleShowActivitySlots },
            "Activity Programme"
          )
        )
      ),
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

var ratingsToPreferences = function (ratingsObj) {
  var ratings = _.pairs(ratingsObj);
  var sortedRatings = _.sortBy(ratings, function (_ref) {
    var _ref2 = _slicedToArray(_ref, 2);

    var key = _ref2[0];
    var value = _ref2[1];
    return -value;
  });
  return new Map(_.zip(sortedRatings.map(function (_ref) {
    var _ref2 = _slicedToArray(_ref, 2);

    var k = _ref2[0];
    var v = _ref2[1];
    return k;
  }), [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]));
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
    if (preference <= 2) {
      theStyle = { color: "green" };
    } else if (preference >= 5) {
      theStyle = { color: "red" };
    }
    return React.createElement(
      "td",
      { style: theStyle },
      display,
      " ",
      React.createElement(
        "span",
        { className: "badge" },
        preference
      )
    );
  }
});

var IndividualEntity = React.createClass({
  displayName: "IndividualEntity",
  render: function () {
    var _props = this.props;
    var activity = _props.activity;
    var preference = _props.preferences.get(activity);
    var className = "label label-default";
    if (preference <= 2) {
      className = "label label-success";
    } else if (preference >= 5) {
      className = "label label-danger";
    }
    return React.createElement(
      "span",
      null,
      _props.display.replace(/ /g, " "),
      " ",
      React.createElement(
        "span",
        { className: "badge" },
        preference
      ),
      " "
    );
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
                  var preferences = ratingsToPreferences(x.ratings);
                  return React.createElement(IndividualEntity, { key: x.name, activity: activity, preferences: preferences, display: x.name });
                })
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
      if (preference <= 2) {
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


React.render(React.createElement(OverallScheduleDisplay, { source: "/data/test.json" }), overallScheduleDisplayNode);