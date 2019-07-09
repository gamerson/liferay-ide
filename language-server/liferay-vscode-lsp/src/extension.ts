/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as net from 'net';
import * as child_process from "child_process";
import { workspace, ExtensionContext } from 'vscode';

import {
	LanguageClient,
	LanguageClientOptions,
	StreamInfo
} from 'vscode-languageclient';

let client: LanguageClient;

function createServer(): Promise<StreamInfo> {
	return new Promise((resolve, reject) => {
		var server = net.createServer((socket) => {
			resolve({
				reader: socket,
				writer: socket
			});

			socket.on('end', () => console.log("Disconnected"));
		}).on('error', (err) => {
			throw err;
		});

		server.listen(() => {
			let options = { cwd: workspace.rootPath };

			let args = [
				'languageServer', '-p', server.address().port.toString()
			]

			child_process.spawn("blade", args, options);
		});
	});
};

export function activate(context: ExtensionContext) {
	let clientOptions: LanguageClientOptions = {
		documentSelector: [{ scheme: 'file', language: 'properties' }],

		synchronize: {
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	client = new LanguageClient(
		'languageServerExample',
		'Language Server Example',
		createServer,
		clientOptions
	);

	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}

	return client.stop();
}