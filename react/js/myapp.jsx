/* global React */
/* global _ */
/* global $:false */
/* global overallScheduleDisplayNode */

const textAlignCenter = { textAlign: "center" };

const ACTIVITIES = ["Archery", "Trail Biking", "Ropes Course", "High Ropes", "Adventure Golf", "Baking", "Crafts",
    "Fire Starter","Video & Photography", "Mental Mayhem", "Indoor Games", "Games Hall", "Football",
    "Adventure Playground"];

let getHashQuery = () => location.hash.replace(/^#/, '');

let OverallScheduleDisplay = React.createClass({
  getInitialState: () => ({
    showing: getHashQuery() || 'preferences',
    isLoading: false,
    plans: []
  }),
  componentDidMount: function () {
    $.ajax({
      dataType: "json",
      url: this.props.source,
      timeout: 120000,
      success: result => {
        this.setState({
          plans: result.plans
        })
      }
    });
    window.onpopstate = event => this.setState({
      showing: getHashQuery()
    });
  },
  render: function () {
    let tabToShow;
    let showing = this.state.showing;
    if (showing == 'activity') {
      tabToShow = (
        <div>
          <h2>Activity Programme</h2>
          <SlotsTableEntity plans={this.state.plans} />
        </div>
      );
    } else if (showing == 'schedules') {
      tabToShow = (
        <div>
          <h2>Individual Schedules</h2>
          <ScheduleTableElement plans={this.state.plans} />
        </div>
      );
    } else if (showing == 'schedules-grouped') {
      tabToShow = (
        <div>
          <h2>Individual Schedules</h2>
          <ScheduleTableGroupedElement plans={this.state.plans} />
        </div>
      );
    } else if (showing == 'preferences') {
      tabToShow = (
        <div>
          <h2>Individual Preferences</h2>
          <IndividualPreferencesEntity plans={this.state.plans} />
        </div>
      );
    } else if (showing == 'prefs-grouped') {
      tabToShow = (
        <div>
          <h2>Individual Preferences Grouped</h2>
          <IndividualPreferencesSortedByGroupEntity plans={this.state.plans} />
        </div>
      );
    } else if (showing == 'upload') {
      tabToShow = (
        <div>
          <h2>Upload Preferences</h2>
          <FileForm url="/data/preferences.csv" parent={this} />
        </div>
      );
    }

    let progressBar;
    if (this.state.isLoading) {
      progressBar = (
        <div>
          <br />
          <div className="well well-sm">
            <p>Uploading new data</p>
            <div className="progress">
              <div className="progress-bar progress-bar-striped active" role="progressbar" ariaValuenow="45" ariaValuemin="0" ariaValuemax="100" style={{width: '45%'}}>
                <span className="sr-only">45% Complete</span>
              </div>
            </div>
          </div>
        </div>
      );
    } else {
      progressBar = <div />;
    }

    return (
      <div>
        <ul className="nav nav-tabs">
          <li role="presentation" className={this.state.showing == 'preferences' ? 'active' : ''}><a href="#preferences">Preferences</a></li>
          <li role="presentation" className={this.state.showing == 'prefs-grouped' ? 'active' : ''}><a href="#prefs-grouped">Preferences Grouped</a></li>
          <li role="presentation" className={this.state.showing == 'schedules' ? 'active' : ''}><a href="#schedules">Schedules</a></li>
          <li role="presentation" className={this.state.showing == 'schedules-grouped' ? 'active' : ''}><a href="#schedules-grouped">Schedules Grouped</a></li>
          <li role="presentation" className={this.state.showing == 'activity' ? 'active' : ''}><a href="#activity">Activity Programme</a></li>
          <li role="presentation" className={this.state.showing == 'upload' ? 'active' : ''}><a href="#upload">Upload Preferences</a></li>
        </ul>
        {progressBar}
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

let ScheduleTableGroupedElement = React.createClass({
  render: function () {
    let sortedPlans = _.sortBy(this.props.plans, plan => [plan.individual.group, plan.individual.name])
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

let ratingsToPreferences = (ratingsObj) => new Map(_.pairs(ratingsObj));

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
    if (preference <= 3) {
      theStyle = { color: "green" };
    } else if (preference >= 5) {
      theStyle = { color: "red" };
    }
    return (
      // <td style={theStyle}>{display} <span className="badge">{preference}</span></td>
      <td style={theStyle}>{display}</td>
    );
  }
})

let IndividualEntity = React.createClass({
  render: function () {
    let _props = this.props;
    return _props.display.replace(/ /g, '\u00a0').join(", ");
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
                    group[1].map( x => x.name.replace(/ /g, '\u00a0')).sort().join(", ")
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

let IndividualPreferencesSortedByGroupEntity = React.createClass({
  render: function () {
    let sortedPlans = _.sortBy(this.props.plans, plan => [plan.individual.group, plan.individual.name])
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
      if (preference <= 3) {
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


// this creates a React component that can be used in other components or
// used directly on the page with React.renderComponent
var FileForm = React.createClass({

  // since we are starting off without any data, there is no initial value
  getInitialState: function() {
    return {
      data_uri: null,
    };
  },

  // prevent form from submitting; we are going to capture the file contents
  handleSubmit: function() {
    let parent = this.props.parent;
    $.ajax({
      url: this.props.url,
      type: "POST",
      timeout: 120000,
      data: this.state.data_uri,
      dataType: "json",
      success: function(data) {
        parent.setState({
          isLoading: false,
          plans: data.plans
        })
      }.bind(this),
      error: function(xhr, status, err) {
        parent.setState({isLoading: false})
      }.bind(this)
    });
    parent.setState({isLoading: true});
    return false;
  },

  // when a file is passed to the input field, retrieve the contents as a
  // base64-encoded data URI and save it to the component's state
  handleFile: function(e) {
    var self = this;
    var reader = new FileReader();
    var file = e.target.files[0];

    reader.onload = function(upload) {
      self.setState({
        data_uri: upload.target.result,
      });
      self.handleSubmit();
    }
    reader.readAsDataURL(file);
},

  // return the structure to display and bind the onChange, onSubmit handlers
  render: function() {
    // since JSX is case sensitive, be sure to use 'encType'
    return (
      <form onSubmit={this.handleSubmit} encType="multipart/form-data">
        <input type="file" onChange={this.handleFile} />
      </form>
    );
  },
});

React.render(
  <OverallScheduleDisplay source="/data/test.json" />,
  overallScheduleDisplayNode
);

