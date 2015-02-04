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
        null,
        this.state.plans.map(function (plan) {
          return React.createElement(IndividualTableLine, { key: plan.individual.name, name: plan.individual.name });
        })
      )
    );
  }
});

var IndividualTableLine = React.createClass({
  displayName: "IndividualTableLine",
  render: function () {
    return React.createElement(
      "tr",
      null,
      React.createElement(
        "td",
        null,
        this.props.name
      )
    );
  }
});

React.render(React.createElement(ScheduleTableElement, { source: "/test.json" }), tableNode);