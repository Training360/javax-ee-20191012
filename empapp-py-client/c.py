import json
import pprint
import sseclient

def with_urllib3(url):
    """Get a streaming response for the given event feed using urllib3."""
    import urllib3
    http = urllib3.PoolManager()
    return http.request('GET', url, preload_content=False)

def with_requests(url):
    """Get a streaming response for the given event feed using requests."""
    import requests
    return requests.get(url, stream=True)

url = 'http://localhost:8080/empapp/api/warning/stream'
response = with_urllib3(url)  # or with_requests(url)
client = sseclient.SSEClient(response)
for event in client.events():
    # pprint.pprint(json.loads(event.data))
    print(event.data)