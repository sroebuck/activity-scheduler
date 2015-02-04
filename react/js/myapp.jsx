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
        <table className="table table-striped table-condensed">
          <thead>
            <tr><th>Name</th><th>Group</th></tr>
          </thead>
          <tbody>{
            this.state.plans.map( plan => {
              let i = plan.individual;
              return <IndividualTableLine key={i.name} name={i.name} group={i.group} ratings={i.ratings} places={plan.places} />;
            })
          }</tbody>
        </table>
      </div>
    );
  }
});

let IndividualTableLine = React.createClass({
  render: function () {
    let _props = this.props;
    let places = new Map(_props.places.map( place => [place.slot, place.activity]));
    let a1 = places.get("11am-12noon");
    let a2 = places.get("12noon-1pm");
    let a3 = places.get("2pm-3pm");
    let a4 = places.get("3pm-4pm");
    return (
      <tr>
        <td>{_props.name}</td>
        <td>{_props.group}</td>
        <td>{a1}</td>
        <td>{a2}</td>
        <td>{a3}</td>
        <td>{a4}</td>
      </tr>
    );
  }
});

React.render(
  <ScheduleTableElement source="/test.json" />,
  tableNode
);