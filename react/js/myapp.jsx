var ExampleApplication = React.createClass({
  render: function () {
    var elapsed = Math.round(this.props.elapsed  / 10);
    var seconds = elapsed / 100 + (elapsed % 100 ? '' : '.00' );
    var message =
      'React has been successfully running for ' + seconds + ' seconds.';

    return <small>{message}</small>;
  }
});

var start = new Date().getTime();
setInterval( () => {
  React.render(
    <ExampleApplication elapsed={new Date().getTime() - start} />,
    document.getElementById('container')
  );
}, 10);

var ScheduleTableElement = React.createClass({
  getInitialState: () => {
      return {
        plans: []
      };
    },
  componentDidMount: function () {
      $.getJSON(this.props.source, function (result) {
        this.setState({
          plans: result.plans
        });
      }.bind(this));
    },
  render: function () {
      return (
        <div>
          <p>There were {this.state.plans.length} individuals plans found.</p>
        </div>
      );
    }
});

React.render(
  <ScheduleTableElement source="/test.json" />,
  tableNode
);