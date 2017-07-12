def appDir = new File(project.properties.get("appDir")).canonicalFile
def serverURL = project.properties.get("signingServerURL")

println appDir

if (appDir.exists() && serverURL != null) {
	println "Calling codesign service..."

	def url = new URL(serverURL + "/codesign")
	def post = url.openConnection()
	def path = appDir.toURI().toASCIIString().replaceAll("^file:","")
	def body = "path=${path}&identity=Developer+ID+Application%3A+Liferay%2C+Inc.+%287H3SPU5TB9%29&dmg=true"

	println("Posting to ${url} with body=${body}")

	post.setRequestMethod("POST")
	post.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
	post.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
	post.setDoOutput(true)
	DataOutputStream wr = new DataOutputStream(post.getOutputStream())
	wr.writeBytes(body)
	wr.flush()
	wr.close()

	def postResponseCode = post.getResponseCode()

	println("ResponseCode=${postResponseCode}")

	if (postResponseCode.equals(200)) {
		println(post.getInputStream().getText())
	}
}