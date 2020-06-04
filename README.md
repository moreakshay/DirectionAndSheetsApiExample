# DirectionAndSheetsApiExample

Google's Direction API and Sheet API were able to be used on Android but due to security issues the are now only server side.  

While configuring the APIs it requires the ip address from where the calls are going to be made, this is where you add the proxy server's ip address.\
and in your proxy server add a method like so\
(Python)
````    def return_json():
        parser = reqparse.RequestParser()
        parser.add_argument('url', required=True)
        args = parser.parse_args()
        url = args['url']
        r = requests.get(url)
        return r.text
````
and check RouteHelper and SheetHelper classes to use them in Android.
