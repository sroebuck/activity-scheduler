/* global React */
/* global _ */
/* global $:false */
/* global overallScheduleDisplayNode */

const textAlignCenter = { textAlign: "center" };

const ACTIVITIES = ["Archery", "Trail Biking", "Ropes Course", "High Ropes", "Adventure Golf", "Baking", "Crafts",
    "Fire Starter","Video & Photography", "Mental Mayhem", "Indoor Games", "Games Hall", "Football",
    "Adventure Playground", "Another"];

let OverallScheduleDisplay = React.createClass({
  getInitialState: () => ({
    showing: 'preferences',
    plans: []
  }),
  componentDidMount: function () {
    $.ajax({
      dataType: "json",
      url: this.props.source,
      timeout: 60000,
      success: result => {
        this.setState({
          plans: result.plans
        })
      }
    });
  },
  handleShowActivitySlots: function (event) {
    this.setState({showing: 'activity'});
  },
  handleShowSchedules: function (event) {
    this.setState({showing: 'schedules'});
  },
  handleShowPreferences: function (event) {
    this.setState({showing: 'preferences'});
  },
  render: function () {
    let tabToShow;
    if (this.state.showing == 'activity') {
      tabToShow = (
        <div>
          <h2>Activity Programme</h2>
          <SlotsTableEntity plans={this.state.plans} />
        </div>
      );
    } else if (this.state.showing == 'schedules') {
      tabToShow = (
        <div>
          <h2>Individual Schedules</h2>
          <ScheduleTableElement plans={this.state.plans} />
        </div>
      );
    } else if (this.state.showing == 'preferences') {
      tabToShow = (
        <div>
          <h2>Individual Preferences</h2>
          <IndividualPreferencesEntity plans={this.state.plans} />
        </div>
      );
    }

    return (
      <div>
        <ul className="nav nav-tabs">
          <li role="presentation" className={this.state.showing == 'preferences' ? 'active' : ''}><a href="#" onClick={this.handleShowPreferences}>Preferences</a></li>
          <li role="presentation" className={this.state.showing == 'schedules' ? 'active' : ''}><a href="#" onClick={this.handleShowSchedules}>Schedules</a></li>
          <li role="presentation" className={this.state.showing == 'activity' ? 'active' : ''}><a href="#" onClick={this.handleShowActivitySlots}>Activity Programme</a></li>
        </ul>
        {tabToShow}
      </div>
    );
  }
})

let ScheduleTableElement = React.createClass({
  render: function () {
    let sortedPlans = _.sortBy(this.props.plans, plan => plan.individual.name)
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

let ratingsToPreferences = (ratingsObj) => {
  let ratings = _.pairs(ratingsObj);
  let sortedRatings = _.sortBy(ratings, ([key,value]) => -value);
  return new Map(_.zip(sortedRatings.map( ([k,v]) => k ), [1,2,3,4,5,6,7,8,9,10,11,12]));
};

let IndividualTableLine = React.createClass({
  render: function () {
    let _props = this.props;
    let preferences = ratingsToPreferences(_props.ratings);
    let places = new Map(_props.places.map( place => [place.slot, place.activity]));
    let a1 = places.get("11am-12noon");
    let a2 = places.get("12noon-1pm");
    let a3 = places.get("2pm-3pm");
    let a4 = places.get("3pm-4pm");
    return (
      <tr>
        <th>{_props.name}</th>
        <td style={textAlignCenter}>{_props.group}</td>
        <ActivityEntity activity={a1} preferences={preferences} display={a1} />
        <ActivityEntity activity={a2} preferences={preferences} display={a2} />
        <ActivityEntity activity={a3} preferences={preferences} display={a3} />
        <ActivityEntity activity={a4} preferences={preferences} display={a4} />
      </tr>
    );
  }
});

let ActivityEntity = React.createClass({
  render: function () {
    let _props = this.props;
    let display = _props.display;
    let activity = _props.activity;
    let preference = _props.preferences.get(activity);
    var theStyle = {};
    if (preference <= 2) {
      theStyle = { color: "green" };
    } else if (preference >= 5) {
      theStyle = { color: "red" };
    }
    return (
      <td style={theStyle}>{display} <span className="badge">{preference}</span></td>
    );
  }
})

let IndividualEntity = React.createClass({
  render: function () {
    let _props = this.props;
    let activity = _props.activity;
    let preference = _props.preferences.get(activity);
    var className = "label label-default";
    if (preference <= 2) {
      className = "label label-success";
    } else if (preference >= 5) {
      className = "label label-danger";
    }
    return (
      <span>{_props.display.replace(/ /g, '\u00a0')}&nbsp;<span className="badge">{preference}</span> </span>
    );
  }
})

let SlotsTableEntity = React.createClass({
  render: function () {
    let plans = this.props.plans.map(plan => plan.places.map( slot => ({
      name: plan.individual.name,
      ratings: plan.individual.ratings,
      activity: slot.activity,
      slot: slot.slot}) ));
    let flattened = _.flatten(plans);
    let grouped = _.groupBy(flattened, entry => entry.slot);
    return (
      <div>
        <SlotTableEntity slot="11am - 12noon" activities={grouped['11am-12noon']} />
        <SlotTableEntity slot="12noon - 1pm" activities={grouped['12noon-1pm']} />
        <SlotTableEntity slot="2pm - 3pm" activities={grouped['2pm-3pm']} />
        <SlotTableEntity slot="3pm - 4pm" activities={grouped['3pm-4pm']} />
      </div>
    );
  }
})

let SlotTableEntity = React.createClass({
  render: function () {
    let activities = this.props.activities;
    let grouped = _.chain(activities).groupBy(entry => entry.activity).pairs().sortBy((activity,entries) => activity).value();
    return (
      <div>
        <h3>{this.props.slot}</h3>
        <table className="table table-striped table-condensed">
          <tbody>
          { grouped.map( group => {
              let activity = group[0];
              return (
                <tr key={activity}><th>{activity.replace(/ /g, '\u00a0')}</th><td>
                  {
                    group[1].map( x => {
                      let preferences = ratingsToPreferences(x.ratings);
                      return <IndividualEntity key={x.name} activity={activity} preferences={preferences} display={x.name} />;
                    })
                  }
                </td></tr>
              );
            })
          }
          </tbody>
        </table>
      </div>
    );
  }
})

let IndividualPreferencesEntity = React.createClass({
  render: function () {
    let sortedPlans = _.sortBy(this.props.plans, plan => plan.individual.name)
    return (
      <div>
        <table className="table table-striped table-condensed table-bordered">
          <thead>
            <tr>
              <th>Name</th>
              <th style={textAlignCenter}>Group</th>
              { ACTIVITIES.map( a => <th style={{textAlign: 'center'}} key={a}>{a}</th> )}
            </tr>
          </thead>
          <tbody>{
            sortedPlans.map( plan => {
              let i = plan.individual;
              return <IndividualPreferencesLine key={i.name} name={i.name} group={i.group} ratings={i.ratings} places={plan.places} />;
            })
          }</tbody>
        </table>
      </div>
    );
  }
})

let IndividualPreferencesLine = React.createClass({
  render: function () {
    let _props = this.props;
    let places = _props.places.map( p => p.activity );
    let preferences = ratingsToPreferences(_props.ratings);
    let ratings = new Map(_.pairs(_props.ratings));
    return (
      <tr>
        <th>{_props.name.replace(/ /g, '\u00a0')}</th>
        <td style={textAlignCenter}>{_props.group}</td>
        { ACTIVITIES.map( a => <PreferenceEntity key={a} activity={a} preferences={preferences} isPlaced={_.includes(places,a)} /> ) }
      </tr>
    );
  }
});

let PreferenceEntity = React.createClass({
  render: function () {
    let _props = this.props;
    let activity = _props.activity;
    let isPlaced = _props.isPlaced;
    let preference = _props.preferences.get(activity);
    let className = '';
    if (isPlaced) {
      if (preference <= 2) {
        className = 'success';
      } else if (preference >= 5) {
        className = 'danger';
      } else {
        className = 'info';
      }
    }
    return (
      <td style={{textAlign: 'center'}} className={className}>{preference}</td>
    );
  }
})


React.render(
  <OverallScheduleDisplay source="/data/test.json" />,
  overallScheduleDisplayNode
);
