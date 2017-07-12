def basedir = project.basedir.canonicalPath
def appDir = new File(new File(basedir, "../com.liferay.ide-repository/target/products/com.liferay.ide.studio/macosx/cocoa/x86_64/DeveloperStudio.app").canonicalPath)
def serverURL = project.properties.get("signing-server-url")

println appDir
println serverURL

if (appDir.exists() && serverURL != null) {
	def url = new URL(serverURL + "/codesign")
	def post = url.openConnection()
	def path = appDir.toURI().toASCIIString().replaceAll("^file:","")
	def body = "path=${path}&identity=Developer+ID+Application%3A+Liferay%2C+Inc.+%287H3SPU5TB9%29"

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