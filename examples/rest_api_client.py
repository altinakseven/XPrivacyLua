#!/usr/bin/env python3
"""
This is an example client for the XPrivacyLua REST API.
It demonstrates how to interact with the API from a Python script.

This can be useful for:
- Automation scripts
- Testing
- Integration with other tools
- Bulk operations

Note: This script needs to run on the Android device or through ADB port forwarding.
For ADB port forwarding, use: adb forward tcp:8271 tcp:8271
"""

import json
import requests
import argparse
import sys
from typing import Dict, Any, Optional, List, Union

class XPrivacyLuaClient:
    """Client for the XPrivacyLua REST API"""
    
    def __init__(self, api_key: str, base_url: str = "http://127.0.0.1:8271/api/v1"):
        """Initialize the client with the API key and base URL"""
        self.api_key = api_key
        self.base_url = base_url
        self.headers = {"X-API-Key": api_key}
    
    def get_restrictions(self) -> Dict[str, Any]:
        """Get all available restrictions"""
        return self._make_request("GET", "/restrictions")
    
    def get_apps(self) -> Dict[str, Any]:
        """Get all apps with their restriction status"""
        return self._make_request("GET", "/apps")
    
    def get_app(self, package_name: str) -> Dict[str, Any]:
        """Get details about a specific app"""
        return self._make_request("GET", f"/apps/{package_name}")
    
    def get_app_restrictions(self, package_name: str) -> Dict[str, Any]:
        """Get restrictions for a specific app"""
        return self._make_request("GET", f"/apps/{package_name}/restrictions")
    
    def set_app_restriction(self, package_name: str, restriction_id: str, enabled: bool) -> Dict[str, Any]:
        """Set a restriction for a specific app"""
        data = {"enabled": enabled}
        return self._make_request("PUT", f"/apps/{package_name}/restrictions/{restriction_id}", data)
    
    def get_location(self, package_name: str) -> Dict[str, Any]:
        """Get the fake location for a specific app"""
        return self._make_request("GET", f"/apps/{package_name}/location")
    
    def set_location(self, package_name: str, latitude: float, longitude: float, 
                    accuracy: Optional[float] = None, altitude: Optional[float] = None,
                    speed: Optional[float] = None, bearing: Optional[float] = None,
                    time: Optional[int] = None) -> Dict[str, Any]:
        """Set a fake location for a specific app"""
        data = {
            "latitude": latitude,
            "longitude": longitude
        }
        
        # Add optional parameters if provided
        if accuracy is not None:
            data["accuracy"] = accuracy
        if altitude is not None:
            data["altitude"] = altitude
        if speed is not None:
            data["speed"] = speed
        if bearing is not None:
            data["bearing"] = bearing
        if time is not None:
            data["time"] = time
            
        return self._make_request("PUT", f"/apps/{package_name}/location", data)
    
    def reset_location(self, package_name: str) -> Dict[str, Any]:
        """Reset the fake location for a specific app"""
        return self._make_request("DELETE", f"/apps/{package_name}/location")
    
    def _make_request(self, method: str, endpoint: str, data: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """Make a request to the API"""
        url = f"{self.base_url}{endpoint}"
        
        try:
            if method == "GET":
                response = requests.get(url, headers=self.headers)
            elif method == "PUT":
                response = requests.put(url, headers=self.headers, json=data)
            elif method == "DELETE":
                response = requests.delete(url, headers=self.headers)
            else:
                raise ValueError(f"Unsupported HTTP method: {method}")
            
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            print(f"Error making request: {e}")
            if hasattr(e, 'response') and e.response is not None:
                try:
                    error_data = e.response.json()
                    print(f"API error: {json.dumps(error_data, indent=2)}")
                except:
                    print(f"Response: {e.response.text}")
            sys.exit(1)

def print_json(data: Dict[str, Any]) -> None:
    """Print JSON data in a readable format"""
    print(json.dumps(data, indent=2))

def main() -> None:
    """Main function to handle command line arguments"""
    parser = argparse.ArgumentParser(description="XPrivacyLua REST API Client")
    parser.add_argument("--api-key", required=True, help="API key for authentication")
    parser.add_argument("--base-url", default="http://127.0.0.1:8271/api/v1", help="Base URL of the API")
    
    subparsers = parser.add_subparsers(dest="command", help="Command to execute")
    
    # Get restrictions
    subparsers.add_parser("get-restrictions", help="Get all available restrictions")
    
    # Get apps
    subparsers.add_parser("get-apps", help="Get all apps with their restriction status")
    
    # Get app
    get_app_parser = subparsers.add_parser("get-app", help="Get details about a specific app")
    get_app_parser.add_argument("package_name", help="Package name of the app")
    
    # Get app restrictions
    get_app_restrictions_parser = subparsers.add_parser("get-app-restrictions", help="Get restrictions for a specific app")
    get_app_restrictions_parser.add_argument("package_name", help="Package name of the app")
    
    # Set app restriction
    set_app_restriction_parser = subparsers.add_parser("set-app-restriction", help="Set a restriction for a specific app")
    set_app_restriction_parser.add_argument("package_name", help="Package name of the app")
    set_app_restriction_parser.add_argument("restriction_id", help="ID of the restriction")
    set_app_restriction_parser.add_argument("--enabled", action="store_true", help="Enable the restriction")
    set_app_restriction_parser.add_argument("--disabled", action="store_true", help="Disable the restriction")
    
    # Get location
    get_location_parser = subparsers.add_parser("get-location", help="Get the fake location for a specific app")
    get_location_parser.add_argument("package_name", help="Package name of the app")
    
    # Set location
    set_location_parser = subparsers.add_parser("set-location", help="Set a fake location for a specific app")
    set_location_parser.add_argument("package_name", help="Package name of the app")
    set_location_parser.add_argument("latitude", type=float, help="Latitude")
    set_location_parser.add_argument("longitude", type=float, help="Longitude")
    set_location_parser.add_argument("--accuracy", type=float, help="Accuracy in meters")
    set_location_parser.add_argument("--altitude", type=float, help="Altitude in meters")
    set_location_parser.add_argument("--speed", type=float, help="Speed in m/s")
    set_location_parser.add_argument("--bearing", type=float, help="Bearing in degrees")
    
    # Reset location
    reset_location_parser = subparsers.add_parser("reset-location", help="Reset the fake location for a specific app")
    reset_location_parser.add_argument("package_name", help="Package name of the app")
    
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        sys.exit(1)
    
    client = XPrivacyLuaClient(args.api_key, args.base_url)
    
    if args.command == "get-restrictions":
        print_json(client.get_restrictions())
    
    elif args.command == "get-apps":
        print_json(client.get_apps())
    
    elif args.command == "get-app":
        print_json(client.get_app(args.package_name))
    
    elif args.command == "get-app-restrictions":
        print_json(client.get_app_restrictions(args.package_name))
    
    elif args.command == "set-app-restriction":
        if args.enabled and args.disabled:
            print("Error: Cannot specify both --enabled and --disabled")
            sys.exit(1)
        elif not args.enabled and not args.disabled:
            print("Error: Must specify either --enabled or --disabled")
            sys.exit(1)
        
        enabled = args.enabled
        print_json(client.set_app_restriction(args.package_name, args.restriction_id, enabled))
    
    elif args.command == "get-location":
        print_json(client.get_location(args.package_name))
    
    elif args.command == "set-location":
        print_json(client.set_location(
            args.package_name,
            args.latitude,
            args.longitude,
            args.accuracy,
            args.altitude,
            args.speed,
            args.bearing
        ))
    
    elif args.command == "reset-location":
        print_json(client.reset_location(args.package_name))

if __name__ == "__main__":
    main()