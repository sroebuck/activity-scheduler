"use strict";

var _slicedToArray = function (arr, i) { if (Array.isArray(arr)) { return arr; } else { var _arr = []; for (var _iterator = arr[Symbol.iterator](), _step; !(_step = _iterator.next()).done;) { _arr.push(_step.value); if (i && _arr.length === i) break; } return _arr; } };

var textAlignCenter = { textAlign: "center" };

var ScheduleTableElement = React.createClass({
  displayName: "ScheduleTableElement",
  getInitialState: function () {
    return { plans: [] };
  },
  componentDidMount: function () {
    var _this = this;
    $.getJSON(this.props.source, function (result) {
      _this.setState({
        plans: result.plans
      });
    });
  },
  render: function () {
    var sortedPlans = _.sortBy(this.state.plans, function (plan) {
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

var IndividualTableLine = React.createClass({
  displayName: "IndividualTableLine",
  render: function () {
    var _props = this.props;
    var ratingsObj = _props.ratings;
    var ratings = Object.keys(ratingsObj).map(function (key) {
      return [key, ratingsObj[key]];
    });
    var sortedRatings = _.sortBy(ratings, function (_ref) {
      var _ref2 = _slicedToArray(_ref, 2);

      var key = _ref2[0];
      var value = _ref2[1];
      return -value;
    });
    var preferences = new Map(_.zip(sortedRatings.map(function (_ref) {
      var _ref2 = _slicedToArray(_ref, 2);

      var k = _ref2[0];
      var v = _ref2[1];
      return k;
    }), [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]));
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
      React.createElement(ActivityEntity, { activity: a1, preferences: preferences }),
      React.createElement(ActivityEntity, { activity: a2, preferences: preferences }),
      React.createElement(ActivityEntity, { activity: a3, preferences: preferences }),
      React.createElement(ActivityEntity, { activity: a4, preferences: preferences })
    );
  }
});

var ActivityEntity = React.createClass({
  displayName: "ActivityEntity",
  render: function () {
    var _props = this.props;
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
      activity,
      " ",
      React.createElement(
        "span",
        { className: "badge" },
        preference
      )
    );
  }
});

React.render(React.createElement(ScheduleTableElement, { source: "/test.json" }), tableNode);