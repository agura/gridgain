#
# Copyright 2019 GridGain Systems, Inc. and Contributors.
# 
# Licensed under the GridGain Community Edition License (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ACLOCAL_AMFLAGS =-I m4

nobase_include_HEADERS = \
	ignite/binary/binary_raw_reader.h \
	ignite/binary/binary_reader.h \
	ignite/binary/binary_writer.h \
	ignite/binary/binary_raw_writer.h \
	ignite/binary/binary_containers.h \
	ignite/binary/binary.h \
	ignite/binary/binary_consts.h \
	ignite/binary/binary_type.h \
	ignite/binary/binary_object.h \
	ignite/impl/binary/binary_type_handler.h \
	ignite/impl/binary/binary_id_resolver.h \
	ignite/impl/binary/binary_type_impl.h \
	ignite/impl/binary/binary_type_manager.h \
	ignite/impl/binary/binary_type_updater.h \
	ignite/impl/binary/binary_common.h \
	ignite/impl/binary/binary_writer_impl.h \
	ignite/impl/binary/binary_field_meta.h \
	ignite/impl/binary/binary_type_snapshot.h \
	ignite/impl/binary/binary_reader_impl.h \
	ignite/impl/binary/binary_schema.h \
	ignite/impl/binary/binary_utils.h \
	ignite/impl/binary/binary_object_header.h \
	ignite/impl/binary/binary_object_impl.h \
	ignite/impl/interop/interop_memory.h \
	ignite/impl/interop/interop.h \
	ignite/impl/interop/interop_stream_position_guard.h \
	ignite/impl/interop/interop_output_stream.h \
	ignite/impl/interop/interop_input_stream.h

uninstall-hook:
	if [ -d ${includedir}/ignite ]; then find ${includedir}/ignite -type d -empty -delete; fi
