/* global React */
/* global _ */
/* global $:false */
/* global tableNode */

const textAlignCenter = { textAlign: "center" };

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
    let sortedPlans = _.sortBy(this.state.plans, plan => plan.individual.name)
    return (
      <div>
        <table className="table table-striped table-condensed">
          <thead>
            <tr>
              <th>Name</th>
              <th style={textAlignCenter}>Group</th>
              <th>11am-12noon</th>
              <th>12noon-1pm</th>
              <th>2pm-3pm</th>
              <th>3pm-4pm</th>
            </tr>
          </thead>
          <tbody>{
            sortedPlans.map( plan => {
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
    let ratingsObj = _props.ratings;
    let ratings = Object.keys(ratingsObj).map( key => [key, ratingsObj[key]]);
    let sortedRatings = _.sortBy(ratings, ([key,value]) => -value);
    let preferences = new Map(_.zip(sortedRatings.map( ([k,v]) => k ), [1,2,3,4,5,6,7,8,9,10,11,12]));
    let places = new Map(_props.places.map( place => [place.slot, place.activity]));
    let a1 = places.get("11am-12noon");
    let a2 = places.get("12noon-1pm");
    let a3 = places.get("2pm-3pm");
    let a4 = places.get("3pm-4pm");
    return (
      <tr>
        <th>{_props.name}</th>
        <td style={textAlignCenter}>{_props.group}</td>
        <ActivityEntity activity={a1} preferences={preferences} />
        <ActivityEntity activity={a2} preferences={preferences} />
        <ActivityEntity activity={a3} preferences={preferences} />
        <ActivityEntity activity={a4} preferences={preferences} />
      </tr>
    );
  }
});

let ActivityEntity = React.createClass({
  render: function () {
    let _props = this.props;
    let activity = _props.activity;
    let preference = _props.preferences.get(activity);
    var theStyle = {};
    if (preference <= 2) {
      theStyle = { color: "green" };
    } else if (preference >= 5) {
      theStyle = { color: "red" };
    }
    return (
      <td style={theStyle}>{activity} <span className="badge">{preference}</span></td>
    );
  }
})

let SlotsTableEntity = React.createClass({
  getInitialState: () => ({ plans: [] }),
  componentDidMount: function () {
    $.getJSON(this.props.source, result => {
      this.setState({
        plans: result.plans
      });
    });
  },
  render: function () {
    // Take this.state.plans
    // Take each plan.places and flatten them into one big list
  }
})

React.render(
  <ScheduleTableElement source="/data/test.json" />,
  tableNode
);
