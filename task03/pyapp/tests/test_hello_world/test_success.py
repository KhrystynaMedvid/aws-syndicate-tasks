import json

from pyapp.tests.test_hello_world import HelloWorldLambdaTestCase


class TestSuccess(HelloWorldLambdaTestCase):

    def test_success(self):
        response = self.HANDLER.handle_request(dict(), dict())
        self.assertEqual(response["statusCode"], 200)
        self.assertEqual(json.loads(response["body"]),
                         {"statusCode": 200, "message": "Hello from Lambda"})