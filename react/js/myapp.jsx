var ExampleApplication = React.createClass({
  render: function() {
    var elapsed = Math.round(this.props.elapsed  / 10);
    var seconds = elapsed / 100 + (elapsed % 100 ? '' : '.00' );
    var message =
      'React has been successfully running for ' + seconds + ' seconds.';

    return <small>{message}</small>;
  }
});

var start = new Date().getTime();
setInterval(function() {
  React.render(
    <ExampleApplication elapsed={new Date().getTime() - start} />,
    document.getElementById('container')
  );
}, 10);
