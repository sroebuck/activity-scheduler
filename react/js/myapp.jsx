let ScheduleTableElement = React.createClass({
  getInitialState: () => ({ plans: [] }),
  componentDidMount: function () {
    $.getJSON(this.props.source, result => {
      this.setState({
        plans: result.plans
      });
    });
  },
  render: function () {
    return (
      <div>
        <p>There were {this.state.plans.length} individuals plans found.</p>
        <table>
        {
          this.state.plans.map( plan =>
            <IndividualTableLine key={plan.individual.name} name={plan.individual.name} />
          )
        }
        </table>
      </div>
    );
  }
});

let IndividualTableLine = React.createClass({
  render: function () {
    return <tr><td>{this.props.name}</td></tr>;
  }
});

React.render(
  <ScheduleTableElement source="/test.json" />,
  tableNode
);