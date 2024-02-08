# The idea is to bulk load RDF files into a SOLID POD
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/journal/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/entry/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/label/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/person/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/place/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/residence/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/thing/
curl -v -X PUT -H 'Link: <http://www.w3.org/ns/ldp#Container>; rel="type"' http://krw.localhost:3000/photo/




