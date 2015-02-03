"use strict";

var ExampleApplication = React.createClass({
  displayName: "ExampleApplication",
  render: function () {
    var elapsed = Math.round(this.props.elapsed / 10);
    var seconds = elapsed / 100 + (elapsed % 100 ? "" : ".00");
    var message = "React has been successfully running for " + seconds + " seconds.";

    return React.createElement(
      "small",
      null,
      message
    );
  }
});

var start = new Date().getTime();
setInterval(function () {
  React.render(React.createElement(ExampleApplication, { elapsed: new Date().getTime() - start }), document.getElementById("container"));
}, 10);

var ScheduleTableElement = React.createClass({
  displayName: "ScheduleTableElement",
  getInitialState: function () {
    return {
      plans: []
    };
  },
  componentDidMount: function () {
    $.getJSON(this.props.source, (function (result) {
      this.setState({
        plans: result.plans
      });
    }).bind(this));
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
      )
    );
  }
});

React.render(React.createElement(ScheduleTableElement, { source: "/test.json" }), tableNode);