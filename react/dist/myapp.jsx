"use strict";

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
    return React.createElement(
      "div",
      null,
      React.createElement(
        "p",
        null,
        "There were ",
        this.state.plans.length,
        " individuals plans found."
      ),
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
              null,
              "Group"
            )
          )
        ),
        React.createElement(
          "tbody",
          null,
          this.state.plans.map(function (plan) {
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
        "td",
        null,
        _props.name
      ),
      React.createElement(
        "td",
        null,
        _props.group
      ),
      React.createElement(
        "td",
        null,
        a1
      ),
      React.createElement(
        "td",
        null,
        a2
      ),
      React.createElement(
        "td",
        null,
        a3
      ),
      React.createElement(
        "td",
        null,
        a4
      )
    );
  }
});

React.render(React.createElement(ScheduleTableElement, { source: "/test.json" }), tableNode);