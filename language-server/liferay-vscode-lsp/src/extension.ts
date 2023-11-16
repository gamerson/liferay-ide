/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

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
			];

			child_process.spawn("blade", args, options);
		});
	});
}

export function activate(context: ExtensionContext) {
	let clientOptions: LanguageClientOptions = {
		documentSelector: [{ scheme: 'file', language: 'properties' }],

		synchronize: {
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	client = new LanguageClient(
		'LiferayLanguageServer',
		'Liferay Language Server',
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